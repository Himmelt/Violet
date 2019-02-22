package org.soraworld.violet.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.hocon.node.Paths;
import org.soraworld.hocon.util.Reflects;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.api.IPlayer;
import org.soraworld.violet.api.ISender;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

public abstract class ICommand {

    public final String name;
    public final String permission;
    public final ICommand parent;
    public final IManager manager;

    protected String usage;
    protected boolean onlyPlayer;
    protected SubExecutor subExecutor;
    protected TabExecutor tabExecutor;
    protected ICommandAdaptor adaptor;
    protected List<String> tabs = new ArrayList<>();
    protected final Map<String, ICommand> subs = new LinkedHashMap<>();

    protected ICommand(@NotNull String name, @Nullable String permission, @Nullable ICommand parent, @NotNull IManager manager) {
        this.name = name;
        this.permission = permission;
        this.parent = parent;
        this.manager = manager;
    }

    public final String getName() {
        return name;
    }

    IManager getManager() {
        return manager;
    }

    public final boolean isOnlyPlayer() {
        return onlyPlayer;
    }

    public void addSub(@NotNull ICommand sub) {
        ICommand old = subs.get(sub.getName());
        if (old != null) {
            subs.entrySet().removeIf(entry -> entry.getValue() == old);
            if (old != sub) old.subs.forEach(sub.subs::putIfAbsent);
        }
        subs.put(sub.getName(), sub);
        for (String alias : sub.getAliases()) {
            subs.putIfAbsent(alias, sub);
        }
    }

    public List<String> getAliases() {
        return new ArrayList<>();
    }

    public void tryAddSub(@NotNull Field field, @NotNull Object instance) {
        IManager manager = getManager();
        Sub sub = field.getAnnotation(Sub.class);
        if (sub == null || Modifier.isStatic(field.getModifiers())) return;
        if (!sub.parent().isEmpty() && !sub.parent().equalsIgnoreCase(getName())) return;

        SubExecutor<ICommand, Object> executor = null;
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
        if (!sub.virtual()) command.subExecutor = executor;
        command.setPermission(perm);
        command.setOnlyPlayer(sub.onlyPlayer() || Reflects.isAssignableFrom(IPlayer.class, params[1]));
        command.setAliases(new ArrayList<>(Arrays.asList(sub.aliases())));
        command.setTabCompletions(sub.tabs());
        command.setUsage(sub.usage());
        command.setDescription(sub.usage());
        command.update();
    }

    /**
     * 发送使用方法.
     *
     * @param sender 信息接收者
     */
    public void sendUsage(ISender sender) {
        String usage = getUsage();
        if (usage != null && !usage.isEmpty()) {
            getManager().sendKey(sender, "cmdUsage", usage);
        }
    }

    public ICommand createSub(Paths paths) {
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
    public ICommand getSub(@NotNull String name) {
        return getSubs().get(name);
    }

    /**
     * 获取父命令
     *
     * @return 父命令
     */
    ICommand getParent() {
        return parent;
    }

    /**
     * 更新命令层次.
     */
    public void update() {
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
    public void execute(Object sender, Args args) {
        SubExecutor executor = getExecutor();
        Map<String, ICommand> subs = getSubs();
        IManager manager = getManager();
        if (executor != null) executor.execute(this, sender, args);
        else if (args.notEmpty()) {
            ICommand sub = subs.get(args.first());
            if (sub != null) {
                if (sub.testPermission(sender)) {
                    args.next();
                    if (sender instanceof IPlayer) sub.execute(sender, args);
                    else if (!sub.isOnlyPlayer()) sub.execute(sender, args);
                    else manager.sendKey(sender, "onlyPlayer");
                } else manager.sendKey(sender, "noCommandPerm", sub.getPermission());
            } else sendUsage(sender);
        } else sendUsage(sender);
    }

    public void handle(@NotNull Object source, @NotNull Args args) {
        if (testPermission(source)) {
            if (source instanceof IPlayer) execute(source, args);
            else if (!isOnlyPlayer()) execute(source, args);
            else getManager().sendKey(source, "onlyPlayer");
        } else getManager().sendKey(source, "noCommandPerm", getPermission());
    }

    public void extractSub(@NotNull Object instance) {
        Field[] fields = instance.getClass().getDeclaredFields();
        if (fields == null || fields.length == 0) return;
        for (Field field : fields) tryAddSub(field, instance);
    }

    public void extractSub(@NotNull Object instance, @NotNull String name) {
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

    public ICommand removeSub(Paths paths) {
        Map<String, ? extends ICommand> subs = getSubs();
        if (paths.hasNext() && subs.containsKey(paths.first())) {
            return subs.get(paths.first()).removeSub(paths.next());
        }
        return subs.remove(paths.first());
    }

    public List<String> tabComplete(Object sender, Args args) {
        String first = args.first();
        List<String> tabs = getTabs();
        Map<String, ? extends ICommand> subs = getSubs();
        if (args.size() == 1) {
            return ICommand.getMatchList(first, tabs != null && !tabs.isEmpty() ? tabs : subs.keySet());
        }
        if (subs.containsKey(first)) {
            args.next();
            return subs.get(first).tabComplete(sender, args);
        }
        return new ArrayList<>();
    }

    public void setTabCompletions(@Nullable String[] tabs) {
        if (tabs != null && tabs.length > 0) {
            setTabs(new ArrayList<>(Arrays.asList(tabs)));
        } else setTabs(null);
    }

    @NotNull
    public static List<String> getMatchList(@NotNull String text, @NotNull Collection<String> possibles) {
        ArrayList<String> list = new ArrayList<>();
        if (text.isEmpty()) list.addAll(possibles);
        else for (String s : possibles) if (s.startsWith(text)) list.add(s);
        return list;
    }
}
