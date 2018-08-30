package org.soraworld.violet.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.soraworld.violet.manager.SpigotManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Spigot 命令.
 */
public class SpigotCommand extends Command {

    /**
     * 是否仅玩家执行.
     */
    private boolean onlyPlayer;
    /**
     * 管理器.
     */
    public final SpigotManager manager;
    /**
     * Tab 补全候选列表.
     */
    protected ArrayList<String> tabs;
    /**
     * 注解执行器.
     */
    protected SpigotExecutor executor;
    /**
     * 子命令映射表.
     */
    protected final HashMap<String, SpigotCommand> subs = new LinkedHashMap<>();
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();
    private static final MethodType INVOKE_EXEC = MethodType.methodType(SpigotExecutor.class);

    /**
     * 实例化 Spigot 命令.
     *
     * @param name       命令主名
     * @param perm       权限
     * @param onlyPlayer 是否仅玩家可执行
     * @param manager    管理器
     * @param aliases    别名
     */
    public SpigotCommand(@Nonnull String name, @Nullable String perm, boolean onlyPlayer, @Nonnull SpigotManager manager, String... aliases) {
        super(name);
        setPermission(perm);
        this.manager = manager;
        this.onlyPlayer = onlyPlayer;
        ArrayList<String> list = new ArrayList<>(Arrays.asList(aliases));
        list.removeIf(s -> s == null || s.isEmpty() || s.contains(" ") || s.contains(":"));
        setAliases(list);
    }

    /**
     * 添加子命令.
     *
     * @param sub 子命令
     */
    public void addSub(SpigotCommand sub) {
        SpigotCommand old = subs.get(sub.getName());
        if (old != null) {
            subs.entrySet().removeIf(entry -> entry.getValue() == old);
            if (old != sub) old.subs.forEach(sub.subs::putIfAbsent);
        }
        subs.put(sub.getName(), sub);
        for (String alias : sub.getAliases()) {
            subs.putIfAbsent(alias, sub);
        }
    }

    /**
     * 获取子命令名列表.
     *
     * @return 列表
     */
    public List<String> subs() {
        return new ArrayList<>(subs.keySet());
    }

    /**
     * 获取子命令.
     *
     * @param name 命令名或别名
     * @return 子命令
     */
    public SpigotCommand getSub(String name) {
        return subs.get(name);
    }

    /**
     * 从类中提取带有 {@link Sub} 注解的静态公开方法，
     * 动态编译成实现了{@link SpigotExecutor}接口的 λ 类，
     * 并封装成{@link SpigotCommand} 注册为子命令.
     * <p>
     * 建议: 为提高检索效率，建议把需要转换的静态方法放到一个单独的类中.
     *
     * @param clazz 检索目标类
     */
    public void extractSub(Class<?> clazz) {
        if (clazz == null || clazz == Object.class || clazz == Class.class) return;
        Method[] methods = clazz.getDeclaredMethods();
        if (methods == null || methods.length == 0) return;
        for (Method method : methods) tryAddSub(method);
    }

    /**
     * 从类中提取带有{@link Sub}注解的，名称为{@code method}的静态公开方法并注册为子命令.
     *
     * @param clazz  检索目标类
     * @param method 目标方法名
     */
    public void extractSub(Class<?> clazz, String method) {
        if (clazz == null || method == null || method.isEmpty() || clazz == Object.class || clazz == Class.class) return;
        try {
            Method theMethod = clazz.getDeclaredMethod(method, SpigotManager.class, CommandSender.class, Paths.class);
            tryAddSub(theMethod);
        } catch (Throwable e) {
            if (manager.isDebug()) e.printStackTrace();
            manager.consoleKey("extractNoSuchSub", clazz.getName(), method);
        }
    }

    private void tryAddSub(@Nonnull Method method) {
        int modifier = method.getModifiers();
        if (!Modifier.isPublic(modifier) || !Modifier.isStatic(modifier)) return;
        Sub sub = method.getAnnotation(Sub.class);
        if (sub == null) return;
        Class<?> ret = method.getReturnType();
        if (!ret.equals(Void.class) && !ret.equals(void.class)) return;
        Class<?>[] params = method.getParameterTypes();
        if (params.length != 3 || params[0] != SpigotCommand.class || params[1] != CommandSender.class || params[2] != Paths.class) return;
        method.setAccessible(true);
        try {
            MethodHandle handle = lookup.unreflect(method);
            CallSite site = LambdaMetafactory.metafactory(
                    lookup,
                    "execute",
                    INVOKE_EXEC,
                    handle.type(),
                    handle,
                    handle.type());
            SpigotExecutor executor = (SpigotExecutor) site.getTarget().invokeExact();
            Paths paths = new Paths(false, sub.paths());
            String name = paths.empty() ? method.getName().toLowerCase() : paths.first().replace(' ', '_').replace(':', '_');
            if (paths.empty()) paths = new Paths(false, name);
            else paths.set(0, name);
            String perm = sub.perm().isEmpty() ? null : sub.perm().replace(' ', '_').replace(':', '_');
            if ("admin".equals(perm)) perm = manager.defAdminPerm();
            SpigotCommand command = getOrCreateSub(paths);
            command.executor = executor;
            command.setPermission(perm);
            command.onlyPlayer = sub.onlyPlayer();
            command.setAliases(new ArrayList<>(Arrays.asList(sub.aliases())));
            command.setTabCompletions(sub.tabs());
            command.usageMessage = sub.usage();
            command.description = sub.usage();
            addSub(command);
        } catch (Throwable e) {
            if (manager.isDebug()) e.printStackTrace();
            manager.consoleKey("extractReflectError", method.getName());
        }
    }

    private SpigotCommand getOrCreateSub(Paths paths) {
        if (paths.empty()) return this;
        SpigotCommand sub = subs.get(paths.first());
        if (sub == null) {
            sub = new SpigotCommand(paths.first(), null, false, manager);
            addSub(sub);
        }
        return sub.getOrCreateSub(paths.next());
    }

    /**
     * 执行命令.
     *
     * @param sender 命令发送者
     * @param args   参数
     */
    public void execute(CommandSender sender, Paths args) {
        if (executor != null) executor.execute(this, sender, args);
        else if (args.notEmpty()) {
            SpigotCommand sub = subs.get(args.first());
            if (sub != null) {
                if (sub.testPermission(sender)) {
                    args.next();
                    if (sender instanceof Player) sub.execute((Player) sender, args);
                    else if (!sub.onlyPlayer) sub.execute(sender, args);
                    else manager.sendKey(sender, "onlyPlayer");
                } else manager.sendKey(sender, "noCommandPerm", sub.getPermission());
            } else sendUsage(sender);
        } else sendUsage(sender);
    }

    /**
     * 执行玩家命令.
     *
     * @param player 玩家
     * @param args   参数
     */
    public void execute(Player player, Paths args) {
        execute((CommandSender) player, args);
    }

    public final boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (testPermission(sender)) {
            if (sender instanceof Player) execute(((Player) sender), new Paths(true, args));
            else if (!onlyPlayer) execute(sender, new Paths(true, args));
            else manager.sendKey(sender, "onlyPlayer");
        } else manager.sendKey(sender, "noCommandPerm", getPermission());
        return true;
    }

    /**
     * 发送使用方法.
     *
     * @param sender 信息接收者
     */
    protected void sendUsage(CommandSender sender) {
        String usage = getUsage();
        if (usage != null && !usage.isEmpty()) {
            manager.sendKey(sender, "cmdUsage", usage);
        }
    }

    /**
     * 获取字符串匹配列表.
     *
     * @param text      待匹配文本
     * @param possibles 待筛选列表
     * @return 匹配后列表
     */
    public static List<String> getMatchList(String text, Collection<String> possibles) {
        ArrayList<String> list = new ArrayList<>();
        if (text.isEmpty()) list.addAll(possibles);
        else for (String s : possibles) if (s.startsWith(text)) list.add(s);
        return list;
    }

    /**
     * 获取tab补全列表.
     *
     * @param args 参数
     * @return 补全列表
     */
    public List<String> tabCompletions(Paths args) {
        String first = args.first();
        if (args.size() == 1) {
            return getMatchList(first, tabs != null && !tabs.isEmpty() ? tabs : subs.keySet());
        }
        if (subs.containsKey(first)) {
            args.next();
            return subs.get(first).tabCompletions(args);
        }
        return new ArrayList<>();
    }

    private void setTabCompletions(String[] tabs) {
        if (tabs != null && tabs.length > 0) {
            this.tabs = new ArrayList<>(Arrays.asList(tabs));
        } else this.tabs = null;
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return tabCompletions(new Paths(true, args));
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) {
        return tabCompletions(new Paths(true, args));
    }

    /**
     * 命令发送者是否能执行此命令.
     *
     * @param target 命令发送者
     * @return 是否能执行此命令
     */
    public final boolean testPermission(CommandSender target) {
        return getPermission() == null || target.hasPermission(getPermission());
    }

    /**
     * 命令发送者是否能执行此命令.
     *
     * @param target 命令发送者
     * @return 是否能执行此命令
     */
    public final boolean testPermissionSilent(CommandSender target) {
        return getPermission() == null || target.hasPermission(getPermission());
    }
}
