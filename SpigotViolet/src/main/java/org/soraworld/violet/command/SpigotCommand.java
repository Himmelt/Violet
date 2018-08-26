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

import static org.soraworld.violet.Violet.*;

/**
 * Spigot 命令.
 */
public class SpigotCommand extends Command {

    public boolean isOnlyPlayer() {
        return onlyPlayer;
    }

    private void setOnlyPlayer(boolean onlyPlayer) {
        this.onlyPlayer = onlyPlayer;
    }

    /**
     * 只有玩家可以执行.
     */
    private boolean onlyPlayer;
    /**
     * 管理器.
     */
    protected final SpigotManager manager;

    protected ArrayList<String> tabs;

    protected Executor executor;

    /**
     * 子命令.
     */
    protected final HashMap<String, SpigotCommand> subs = new LinkedHashMap<>();

    private static final MethodType METHOD_VOID = MethodType.methodType(void.class);
    private static final MethodType INVOKE_EXEC = MethodType.methodType(Executor.class);

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
        if (tabs != null && !tabs.isEmpty()) return tabs;
        String first = args.first();
        if (args.size() == 1) return getMatchList(first, subs.keySet());
        if (subs.containsKey(first)) {
            args.next();
            return subs.get(first).tabCompletions(args);
        }
        return new ArrayList<>();
    }

    public void setTabCompletions(String[] tabs) {
        if (tabs != null && tabs.length > 0) {
            this.tabs = new ArrayList<>(Arrays.asList(tabs));
        }
    }

    /**
     * 添加子命令.
     *
     * @param sub 子命令
     */
    public void addSub(SpigotCommand sub) {
        subs.put(sub.getName(), sub);
        for (String alias : sub.getAliases()) {
            subs.putIfAbsent(alias, sub);
        }
    }

    /* TODO 搞一个类 专门注解存储方法 然后 提取那个类 或 其实例*/
    public void extractSub(Object target) {
        if (target == null) return;
        boolean isClass = target instanceof Class;
        ArrayList<Method> methods = getMethods(isClass ? (Class<?>) target : target.getClass());
        for (Method method : methods) {
            boolean isStatic = Modifier.isStatic(method.getModifiers());
            if (isClass && !isStatic) continue;
            Sub sub = method.getAnnotation(Sub.class);
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            try {
                MethodHandle handle = lookup.unreflect(method);
                //implArity + receiverArity != capturedArity + samArity
                CallSite site = LambdaMetafactory.metafactory(lookup, "execute", INVOKE_EXEC, METHOD_VOID, handle, METHOD_VOID);
                Executor executor = isStatic ? (Executor) site.getTarget().invokeExact() : (Executor) site.getTarget().invokeExact(target);
                Paths paths = new Paths(sub.paths());
                String name = paths.empty() ? method.getName().toLowerCase() : paths.first().replace(' ', '_').replace(':', '_');
                if (paths.empty()) paths = new Paths(name);
                else paths.set(0, name);
                String perm = sub.perm().isEmpty() ? null : sub.perm().replace(' ', '_').replace(':', '_');
                SpigotCommand command = getOrCreateSub(paths);
                command.executor = executor;
                command.setPermission(perm);
                command.onlyPlayer = sub.onlyPlayer();
                command.setAliases(new ArrayList<>(Arrays.asList(sub.aliases())));
                command.setTabCompletions(sub.tabs());
                command.setUsage(sub.usage());
                addSub(command);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<Method> getMethods(Class<?> clazz) {
        if (clazz == null || clazz == Object.class || clazz == Class.class) return new ArrayList<>();
        ArrayList<Method> list = new ArrayList<>();
        Method[] methods = clazz.getDeclaredMethods();
        if (methods != null && methods.length > 0) {
            for (Method method : methods) {
                if (!Modifier.isPublic(method.getModifiers())) continue;
                Sub sub = method.getAnnotation(Sub.class);
                if (sub == null) continue;
                Class<?> ret = method.getReturnType();
                if (!ret.equals(Void.class) && !ret.equals(void.class)) continue;
                Class<?>[] params = method.getParameterTypes();
                if (params.length == 2 && params[0] == CommandSender.class && params[1] == CommandArgs.class) {
                    method.setAccessible(true);
                    list.add(method);
                }
            }
        }
        list.addAll(getMethods(clazz.getSuperclass()));
        return list;
    }

    public SpigotCommand getOrCreateSub(Paths paths) {
        if (paths.empty()) return this;
        SpigotCommand sub = subs.get(paths.first());
        if (sub == null) sub = new SpigotCommand(paths.first(), null, false, manager);
        return sub.getOrCreateSub(paths.next());
    }

    /**
     * 执行命令.
     *
     * @param sender 命令发送者
     * @param args   参数
     */
    public void execute(CommandSender sender, CommandArgs args) {
        if (executor != null) executor.execute(sender, args);
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

    /**
     * Violet 命令.
     */
    public static class CommandViolet extends SpigotCommand {
        /**
         * 实例化 violet 命令.
         *
         * @param name       命令主名
         * @param perm       权限
         * @param onlyPlayer 是否仅玩家执行
         * @param manager    管理器
         * @param aliases    别名
         */
        public CommandViolet(@Nonnull String name, @Nullable String perm, boolean onlyPlayer, @Nonnull SpigotManager manager, String... aliases) {
            super(name, perm, onlyPlayer, manager, aliases);
            addSub(new SpigotCommand("lang", manager.defAdminPerm(), false, manager) {
                public void execute(CommandSender sender, CommandArgs args) {
                    if (args.notEmpty()) {
                        if (manager.setLang(args.first())) {
                            manager.asyncSave();
                            manager.sendKey(sender, KEY_SET_LANG, manager.getLang());
                        } else {
                            manager.sendKey(sender, KEY_SET_LANG_FAILED, args.first());
                        }
                    } else manager.sendKey(sender, KEY_GET_LANG, manager.getLang());
                }
            });
            addSub(new SpigotCommand("save", manager.defAdminPerm(), false, manager) {
                public void execute(CommandSender sender, CommandArgs args) {
                    manager.sendKey(sender, manager.save() ? KEY_CFG_SAVE : KEY_CFG_SAVE_FAIL);
                }
            });
            addSub(new SpigotCommand("debug", manager.defAdminPerm(), false, manager) {
                public void execute(CommandSender sender, CommandArgs args) {
                    manager.setDebug(!manager.isDebug());
                    manager.sendKey(sender, manager.isDebug() ? KEY_DEBUG_ON : KEY_DEBUG_OFF);
                }
            });
            addSub(new SpigotCommand("reload", manager.defAdminPerm(), false, manager) {
                public void execute(CommandSender sender, CommandArgs args) {
                    manager.sendKey(sender, manager.load() ? KEY_CFG_LOAD : KEY_CFG_LOAD_FAIL);
                }
            });
        }

        public String getUsage() {
            return "/violet lang|debug|save|reload";
        }
    }
}
