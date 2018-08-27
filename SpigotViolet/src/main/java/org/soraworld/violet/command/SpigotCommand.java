package org.soraworld.violet.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.soraworld.violet.Violet;
import org.soraworld.violet.manager.SpigotManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.soraworld.violet.Violet.KEY_CMD_USAGE;

/**
 * Spigot 命令.
 */
public class SpigotCommand extends Command {

    /**
     * 只有玩家可以执行.
     */
    private boolean onlyPlayer;
    /**
     * 管理器.
     */
    protected final SpigotManager manager;

    protected ArrayList<String> tabs;

    protected SpigotExecutor executor;

    /**
     * 子命令.
     */
    protected final HashMap<String, SpigotCommand> subs = new LinkedHashMap<>();
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();
    private static final MethodType INVOKE_EXEC = MethodType.methodType(SpigotExecutor.class);

    /* 注册的 参数里可以加上方法名，根据方法名查找更快*/
    // 先获取 class 下所有的 @Sub Method

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
     * 获取tab补全列表.
     *
     * @param args 参数
     * @return 补全列表
     */
    public List<String> tabCompletions(CommandArgs args) {
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

    public void setTabCompletions(String[] tabs) {
        if (tabs != null && tabs.length > 0) {
            this.tabs = new ArrayList<>(Arrays.asList(tabs));
        } else this.tabs = null;
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

    /* TODO javadoc 搞一个类 专门注解存储方法 然后 提取那个类 或 其实例*/
    public void extractSub(Class<?> clazz) {
        if (clazz == null || clazz == Object.class || clazz == Class.class) return;
        Method[] methods = clazz.getDeclaredMethods();
        if (methods == null || methods.length == 0) return;
        for (Method method : methods) tryAddSub(method);
    }

    public void extractSub(Class<?> clazz, String method) {
        if (clazz == null || method == null || method.isEmpty() || clazz == Object.class || clazz == Class.class) return;
        try {
            Method theMethod = clazz.getDeclaredMethod(method, SpigotManager.class, CommandSender.class, CommandArgs.class);
            tryAddSub(theMethod);
        } catch (Throwable e) {
            if (manager.isDebug()) e.printStackTrace();
            manager.consoleKey("extractNoSuchSub", clazz.getName(), method);
        }
    }

    private void tryAddSub(Method method) {
        int modifier = method.getModifiers();
        if (!Modifier.isPublic(modifier) || !Modifier.isStatic(modifier)) return;
        Sub sub = method.getAnnotation(Sub.class);
        if (sub == null) return;
        Class<?> ret = method.getReturnType();
        if (!ret.equals(Void.class) && !ret.equals(void.class)) return;
        Class<?>[] params = method.getParameterTypes();
        if (params.length != 3 || params[0] != SpigotManager.class || params[1] != CommandSender.class || params[2] != CommandArgs.class) return;
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
            Paths paths = new Paths(sub.paths());
            String name = paths.empty() ? method.getName().toLowerCase() : paths.first().replace(' ', '_').replace(':', '_');
            if (paths.empty()) paths = new Paths(name);
            else paths.set(0, name);
            String perm = sub.perm().isEmpty() ? null : sub.perm().replace(' ', '_').replace(':', '_');
            if ("admin".equals(perm)) perm = manager.defAdminPerm();
            SpigotCommand command = getOrCreateSub(paths);
            command.executor = executor;
            command.setPermission(perm);
            command.onlyPlayer = sub.onlyPlayer();
            command.setAliases(new ArrayList<>(Arrays.asList(sub.aliases())));
            command.setTabCompletions(sub.tabs());
            command.setUsage(sub.usage());
            addSub(command);
        } catch (Throwable e) {
            if (manager.isDebug()) e.printStackTrace();
            manager.consoleKey("extractReflectError");
        }
    }

    public SpigotCommand getOrCreateSub(Paths paths) {
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
    public void execute(CommandSender sender, CommandArgs args) {
        if (executor != null) executor.execute(manager, sender, args);
        else if (args.notEmpty()) {
            SpigotCommand sub = subs.get(args.first());
            if (sub != null) {
                if (sub.testPermission(sender)) {
                    args.next();
                    if (sender instanceof Player) sub.execute((Player) sender, args);
                    else if (!sub.onlyPlayer) sub.execute(sender, args);
                    else manager.sendKey(sender, Violet.KEY_ONLY_PLAYER);
                } else manager.sendKey(sender, Violet.KEY_NO_CMD_PERM, sub.getPermission());
            } else sendUsage(sender);
        } else sendUsage(sender);
    }

    /**
     * 执行玩家命令.
     *
     * @param player 玩家
     * @param args   参数
     */
    public void execute(Player player, CommandArgs args) {
        execute((CommandSender) player, args);
    }

    /**
     * 发送使用方法.
     *
     * @param sender 信息接收者
     */
    protected void sendUsage(CommandSender sender) {
        String usage = getUsage();
        if (usage != null && !usage.isEmpty()) {
            manager.sendKey(sender, KEY_CMD_USAGE, usage);
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

    public final boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (testPermission(sender)) {
            if (sender instanceof Player) execute(((Player) sender), new CommandArgs(args));
            else if (!onlyPlayer) execute(sender, new CommandArgs(args));
            else manager.sendKey(sender, Violet.KEY_ONLY_PLAYER);
        } else manager.sendKey(sender, Violet.KEY_NO_CMD_PERM, getPermission());
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return tabCompletions(new CommandArgs(args));
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) {
        return tabCompletions(new CommandArgs(args));
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
