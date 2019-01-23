package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.hocon.node.Paths;
import org.soraworld.hocon.util.Reflects;
import org.soraworld.violet.command.Args;
import org.soraworld.violet.command.Sub;
import org.soraworld.violet.command.SubExecutor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

public interface ICommand {
    @NotNull
    ICommand build(@Nullable ICommand parent, @NotNull String name, @Nullable String perm, boolean onlyPlayer, @NotNull IManager manager);

    String getName();

    IManager getManager();

    boolean isOnlyPlayer();

    void setOnlyPlayer(boolean onlyPlayer);

    List<String> getTabs();

    void setTabs(List<String> tabs);

    void setExecutor(SubExecutor<ICommand, ISender> executor);

    SubExecutor<ICommand, ISender> getExecutor();

    Map<String, ICommand> getSubs();

    default void addSub(@NotNull ICommand sub) {
        Map<String, ICommand> subs = getSubs();
        ICommand old = subs.get(sub.getName());
        if (old != null) {
            subs.entrySet().removeIf(entry -> entry.getValue() == old);
            if (old != sub) old.getSubs().forEach(sub.getSubs()::putIfAbsent);
        }
        subs.put(sub.getName(), sub);
        for (String alias : sub.getAliases()) {
            subs.putIfAbsent(alias, sub);
        }
    }

    default void tryAddSub(@NotNull Field field, @NotNull Object instance) {
        IManager manager = getManager();
        Sub sub = field.getAnnotation(Sub.class);
        if (sub == null || Modifier.isStatic(field.getModifiers())) return;
        if (!sub.parent().isEmpty() && !sub.parent().equalsIgnoreCase(getName())) return;

        SubExecutor<ICommand, ISender> executor = null;
        try {
            executor = (SubExecutor) field.get(instance);
        } catch (Throwable e) {
            if (manager.isDebug()) e.printStackTrace();
        }
        if (executor == null) return;

        Type[] params = Reflects.getActualTypes(SubExecutor.class, executor.getClass());
        if (params == null || params.length != 2
                || !Reflects.isAssignableFrom(params[0], getClass())
                || !Reflects.isAssignableFrom(ICommand.class, params[0])
                || !Reflects.isAssignableFrom(ISender.class, params[1])) return;

        Paths paths = new Paths(sub.path().isEmpty() ? field.getName().toLowerCase() : sub.path().replace(' ', '_').replace(':', '_'));
        String perm = sub.perm().isEmpty() ? null : sub.perm().replace(' ', '_').replace(':', '_');
        if ("admin".equals(perm)) perm = manager.defAdminPerm();
        ICommand command = createSub(paths);
        if (!sub.virtual()) command.setExecutor(executor);
        command.setPermission(perm);
        command.setOnlyPlayer(sub.onlyPlayer() || Reflects.isAssignableFrom(IPlayer.class, params[1]));
        command.setAliases(new ArrayList<>(Arrays.asList(sub.aliases())));
        command.setTabCompletions(sub.tabs());
        command.setUsage(sub.usage());
        command.setDescription(sub.usage());
        command.update();
    }

    Object setDescription(String description);

    String getUsage();

    Object setUsage(String usage);

    /**
     * 发送使用方法.
     *
     * @param sender 信息接收者
     */
    default void sendUsage(ISender sender) {
        String usage = getUsage();
        if (usage != null && !usage.isEmpty()) {
            getManager().sendKey(sender, "cmdUsage", usage);
        }
    }

    List<String> getAliases();

    Object setAliases(List<String> aliases);

    String getPermission();

    void setPermission(String permission);

    default boolean testPermission(@NotNull ISender sender) {
        return sender.hasPermission(getPermission());
    }

    default ICommand createSub(Paths paths) {
        Map<String, ICommand> subs = getSubs();
        if (paths.empty()) return this;
        ICommand sub = subs.get(paths.first());
        if (sub == null) sub = build(this, paths.first(), null, false, getManager());
        return sub.createSub(paths.next());
    }

    /**
     * 获取子命令.
     *
     * @param name 命令名或别名
     * @return 子命令
     */
    default ICommand getSub(@NotNull String name) {
        return getSubs().get(name);
    }

    /**
     * 获取父命令
     *
     * @return 父命令
     */
    ICommand getParent();

    /**
     * 更新命令层次.
     */
    default void update() {
        ICommand parent = getParent();
        if (parent != null) {
            parent.addSub(this);
            parent.update();
        }
    }

    /**
     * 执行命令.
     *
     * @param sender 命令发送者
     * @param args   参数
     */
    default void execute(ISender sender, Args args) {
        SubExecutor<ICommand, ISender> executor = getExecutor();
        Map<String, ICommand> subs = getSubs();
        IManager manager = getManager();
        if (executor != null) executor.execute(this, sender, args);
        else if (args.notEmpty()) {
            ICommand sub = subs.get(args.first());
            if (sub != null) {
                if (sub.testPermission(sender)) {
                    args.next();
                    if (sender instanceof IPlayer) sub.execute((IPlayer) sender, args);
                    else if (!sub.isOnlyPlayer()) sub.execute(sender, args);
                    else manager.sendKey(sender, "onlyPlayer");
                } else manager.sendKey(sender, "noCommandPerm", sub.getPermission());
            } else sendUsage(sender);
        } else sendUsage(sender);
    }

    default void handle(@NotNull ISender source, @NotNull Args args) {
        if (testPermission(source)) {
            if (source instanceof IPlayer) execute((IPlayer) source, args);
            else if (!isOnlyPlayer()) execute(source, args);
            else getManager().sendKey(source, "onlyPlayer");
        } else getManager().sendKey(source, "noCommandPerm", getPermission());
    }

    /**
     * 执行玩家命令.
     *
     * @param player 玩家
     * @param args   参数
     */
    default void execute(IPlayer player, Args args) {
        execute((ISender) player, args);
    }

    default void extractSub(@NotNull Object instance) {
        Field[] fields = instance.getClass().getDeclaredFields();
        if (fields == null || fields.length == 0) return;
        for (Field field : fields) tryAddSub(field, instance);
    }

    /**
     * 从类中提取带有{@link Sub}注解的，名称为{@code field}的静态字段并注册为子命令.
     *
     * @param instance 检索目标实例
     * @param name     目标字段名
     */
    default void extractSub(@NotNull Object instance, @NotNull String name) {
        if (name.isEmpty()) return;
        try {
            Field field = instance.getClass().getDeclaredField(name);
            tryAddSub(field, instance);
        } catch (Throwable e) {
            IManager manager = getManager();
            if (manager.isDebug()) e.printStackTrace();
            manager.consoleKey("extractNoSuchSub", instance.getClass().getName(), name);
        }
    }

    default void removeSub(Paths paths) {
        Map<String, ? extends ICommand> subs = getSubs();
        if (paths.hasNext() && subs.containsKey(paths.first())) {
            subs.get(paths.first()).removeSub(paths.next());
        }
        subs.remove(paths.first());
    }

    /**
     * 获取tab补全列表.
     *
     * @param args 参数
     * @return 补全列表
     */
    default List<String> tabCompletions(Args args) {
        String first = args.first();
        List<String> tabs = getTabs();
        Map<String, ? extends ICommand> subs = getSubs();
        if (args.size() == 1) {
            return ICommand.getMatchList(first, tabs != null && !tabs.isEmpty() ? tabs : subs.keySet());
        }
        if (subs.containsKey(first)) {
            args.next();
            return subs.get(first).tabCompletions(args);
        }
        return new ArrayList<>();
    }

    /**
     * 设置命令补全.
     *
     * @param tabs 补全列表
     */
    default void setTabCompletions(@Nullable String[] tabs) {
        if (tabs != null && tabs.length > 0) {
            setTabs(new ArrayList<>(Arrays.asList(tabs)));
        } else setTabs(null);
    }

    /**
     * 获取字符串匹配列表.
     *
     * @param text      待匹配文本
     * @param possibles 待筛选列表
     * @return 匹配后列表
     */
    static List<String> getMatchList(String text, Collection<String> possibles) {
        ArrayList<String> list = new ArrayList<>();
        if (text.isEmpty()) list.addAll(possibles);
        else for (String s : possibles) if (s.startsWith(text)) list.add(s);
        return list;
    }
}
