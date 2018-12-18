package org.soraworld.violet.command;

import net.jodah.typetools.TypeResolver;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.soraworld.hocon.node.Paths;
import org.soraworld.violet.manager.SpigotManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Spigot 命令.
 */
public class SpigotCommand extends Command {

    /**
     * 管理器.
     */
    protected final SpigotManager manager;
    /**
     * 是否仅玩家执行.
     */
    protected boolean onlyPlayer;
    /**
     * 父命令.
     */
    protected final SpigotCommand parent;
    /**
     * Tab 补全候选列表.
     */
    protected ArrayList<String> tabs;
    /**
     * 注解执行器.
     */
    protected SubExecutor executor;
    /**
     * 子命令映射表.
     */
    protected final HashMap<String, SpigotCommand> subs = new LinkedHashMap<>();

    /**
     * 实例化 Spigot 命令.
     *
     * @param name       命令主名
     * @param perm       权限
     * @param onlyPlayer 是否仅玩家可执行
     * @param manager    管理器
     * @param aliases    别名
     */
    public SpigotCommand(String name, String perm, boolean onlyPlayer, SpigotManager manager, String... aliases) {
        this(null, name, perm, onlyPlayer, manager, aliases);
    }

    /**
     * 实例化 Spigot 命令.
     *
     * @param parent     父命令
     * @param name       命令主名
     * @param perm       权限
     * @param onlyPlayer 是否仅玩家可执行
     * @param manager    管理器
     * @param aliases    别名
     */
    public SpigotCommand(SpigotCommand parent, String name, String perm, boolean onlyPlayer, SpigotManager manager, String... aliases) {
        super(name);
        setPermission(perm);
        this.parent = parent;
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

    public void removeSub(Paths paths) {
        if (paths.hasNext() && subs.containsKey(paths.first())) {
            subs.get(paths.first()).removeSub(paths.next());
        }
        subs.remove(paths.first());
    }

    /**
     * 获取父命令
     *
     * @return 父命令
     */
    public SpigotCommand getParent() {
        return parent;
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
        if (clazz == null) return;
        Field[] fields = clazz.getDeclaredFields();
        if (fields == null || fields.length == 0) return;
        for (Field field : fields) tryAddSub(field);
    }

    /**
     * 从类中提取带有{@link Sub}注解的，名称为{@code method}的静态公开方法并注册为子命令.
     *
     * @param clazz 检索目标类
     * @param field 目标方法名
     */
    public void extractSub(Class<?> clazz, String field) {
        if (clazz == null || field == null || field.isEmpty()) return;
        try {
            Field theField = clazz.getDeclaredField(field);
            tryAddSub(theField);
        } catch (Throwable e) {
            if (manager.isDebug()) e.printStackTrace();
            manager.consoleKey("extractNoSuchSub", clazz.getName(), field);
        }
    }

    private void tryAddSub(Field field) {
        int modifier = field.getModifiers();
        /* public static final SubExecutor<> field */
        if (!Modifier.isPublic(modifier) || !Modifier.isStatic(modifier) || !Modifier.isFinal(modifier)) return;
        Sub sub = field.getAnnotation(Sub.class);
        if (sub == null) return;
        if (!sub.parent().isEmpty() && !sub.parent().equalsIgnoreCase(getName())) return;
        SubExecutor executor = null;
        try {
            executor = (SubExecutor) field.get(null);
        } catch (Throwable e) {
            if (manager.isDebug()) e.printStackTrace();
        }
        if (executor == null) return;

        Class<?>[] params = TypeResolver.resolveRawArguments(SubExecutor.class, executor.getClass());
        if (params.length != 3
                || !SpigotCommand.class.isAssignableFrom(params[0])
                || !SpigotManager.class.isAssignableFrom(params[1])
                || !CommandSender.class.isAssignableFrom(params[2])) return;

        Paths paths = new Paths(sub.path().isEmpty() ? field.getName().toLowerCase() : sub.path().replace(' ', '_').replace(':', '_'));
        String perm = sub.perm().isEmpty() ? null : sub.perm().replace(' ', '_').replace(':', '_');
        if ("admin".equals(perm)) perm = manager.defAdminPerm();
        // 此处返回的是最终命令
        SpigotCommand command = createSub(paths);
        if (!sub.virtual()) command.executor = executor;
        command.setPermission(perm);
        command.onlyPlayer = sub.onlyPlayer() || Player.class.isAssignableFrom(params[2]);
        command.setAliases(new ArrayList<>(Arrays.asList(sub.aliases())));
        command.setTabCompletions(sub.tabs());
        command.usageMessage = sub.usage();
        command.description = sub.usage();
        command.update();
    }

    // 返回的是最终命令
    private SpigotCommand createSub(Paths paths) {
        if (paths.empty()) return this;
        SpigotCommand sub = subs.get(paths.first());
        if (sub == null) sub = new SpigotCommand(this, paths.first(), null, false, manager);
        return sub.createSub(paths.next());
    }

    /**
     * 更新命令层次.
     */
    public void update() {
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
    public void execute(CommandSender sender, Args args) {
        if (executor != null) executor.execute(this, manager, sender, args);
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
    public void execute(Player player, Args args) {
        execute((CommandSender) player, args);
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if (testPermission(sender)) {
            if (sender instanceof Player) execute(((Player) sender), new Args(args));
            else if (!onlyPlayer) execute(sender, new Args(args));
            else manager.sendKey(sender, "onlyPlayer");
        } else manager.sendKey(sender, "noCommandPerm", getPermission());
        return true;
    }

    /**
     * 发送使用方法.
     *
     * @param sender 信息接收者
     */
    public void sendUsage(CommandSender sender) {
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
    public List<String> tabCompletions(Args args) {
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

    /**
     * 设置命令补全.
     *
     * @param tabs 补全列表
     */
    public void setTabCompletions(String[] tabs) {
        if (tabs != null && tabs.length > 0) {
            this.tabs = new ArrayList<>(Arrays.asList(tabs));
        } else this.tabs = null;
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return tabCompletions(new Args(args));
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) {
        return tabCompletions(new Args(args));
    }

    /**
     * 命令发送者是否能执行此命令.
     *
     * @param target 命令发送者
     * @return 是否能执行此命令
     */
    public boolean testPermission(CommandSender target) {
        return getPermission() == null || target.hasPermission(getPermission());
    }

    /**
     * 命令发送者是否能执行此命令.
     *
     * @param target 命令发送者
     * @return 是否能执行此命令
     */
    public boolean testPermissionSilent(CommandSender target) {
        return getPermission() == null || target.hasPermission(getPermission());
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof SpigotCommand) return getName().equalsIgnoreCase(((SpigotCommand) obj).getName());
        return false;
    }
}
