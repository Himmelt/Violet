package org.soraworld.violet.command;

import org.soraworld.violet.manager.SpongeManager;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Sponge 命令.
 */
public class SpongeCommand implements CommandCallable {

    /**
     * 命令主名.
     */
    public final String name;
    /**
     * 管理器.
     */
    public final SpongeManager manager;

    /**
     * 命令权限.
     */
    protected String perm;
    /**
     * 是否仅玩家执行.
     */
    protected boolean onlyPlayer;
    /**
     * 父命令.
     */
    protected final SpongeCommand parent;
    /**
     * Tab 补全候选列表.
     */
    protected ArrayList<String> tabs;
    /**
     * 注解执行器.
     */
    protected SpongeExecutor executor;
    /**
     * 别名列表.
     */
    protected List<String> aliases = new ArrayList<>();
    /**
     * 用法.
     */
    protected String usage;
    /**
     * 子命令映射表.
     */
    protected final HashMap<String, SpongeCommand> subs = new LinkedHashMap<>();
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();
    private static final MethodType INVOKE_EXEC = MethodType.methodType(SpongeExecutor.class);

    /**
     * 实例化 Sponge 命令.
     *
     * @param name       命令主名
     * @param perm       权限
     * @param onlyPlayer 是否仅玩家可执行
     * @param manager    管理器
     * @param aliases    别名
     */
    public SpongeCommand(@Nonnull String name, @Nullable String perm, boolean onlyPlayer, @Nonnull SpongeManager manager, String... aliases) {
        this(null, name, perm, onlyPlayer, manager, aliases);
    }

    /**
     * 实例化 Sponge 命令.
     *
     * @param parent     父命令
     * @param name       命令主名
     * @param perm       权限
     * @param onlyPlayer 是否仅玩家可执行
     * @param manager    管理器
     * @param aliases    别名
     */
    public SpongeCommand(@Nullable SpongeCommand parent, @Nonnull String name, @Nullable String perm, boolean onlyPlayer, @Nonnull SpongeManager manager, String... aliases) {
        this.name = name;
        this.perm = perm;
        this.parent = parent;
        this.manager = manager;
        this.onlyPlayer = onlyPlayer;
        this.aliases.add(name);
        this.aliases.addAll(Arrays.asList(aliases));
        this.aliases.removeIf(s -> s == null || s.isEmpty() || s.contains(" ") || s.contains(":"));
    }

    /**
     * 添加子命令.
     *
     * @param sub 子命令
     */
    public void addSub(SpongeCommand sub) {
        SpongeCommand old = subs.get(sub.name);
        if (old != null) {
            subs.entrySet().removeIf(entry -> entry.getValue() == old);
            if (old != sub) old.subs.forEach(sub.subs::putIfAbsent);
        }
        subs.put(sub.name, sub);
        for (String alias : sub.aliases) {
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
    public SpongeCommand getSub(String name) {
        return subs.get(name);
    }

    /**
     * 获取父命令
     *
     * @return 父命令
     */
    public SpongeCommand getParent() {
        return parent;
    }

    /**
     * 从类中提取带有 {@link Sub} 注解的静态公开方法，
     * 动态编译成实现了{@link SpongeExecutor}接口的 λ 类，
     * 并封装成{@link SpongeCommand} 注册为子命令.
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
            Method theMethod = clazz.getDeclaredMethod(method, SpongeCommand.class, CommandSource.class, Paths.class);
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
        if (params.length != 3 || params[0] != SpongeCommand.class || params[1] != CommandSource.class || params[2] != Paths.class) return;
        method.setAccessible(true);
        Paths paths = new Paths(false, sub.paths());
        String name = paths.empty() ? method.getName().toLowerCase() : paths.first().replace(' ', '_').replace(':', '_');
        if (paths.empty()) paths = new Paths(false, name);
        else paths.set(0, name);
        String perm = sub.perm().isEmpty() ? null : sub.perm().replace(' ', '_').replace(':', '_');
        if ("admin".equals(perm)) perm = manager.defAdminPerm();
        // 此处返回的是最终命令
        SpongeCommand command = createSub(paths);
        if (!sub.virtual()) {
            try {
                MethodHandle handle = lookup.unreflect(method);
                CallSite site = LambdaMetafactory.metafactory(
                        lookup,
                        "execute",
                        INVOKE_EXEC,
                        handle.type(),
                        handle,
                        handle.type());
                command.executor = (SpongeExecutor) site.getTarget().invokeExact();
            } catch (Throwable e) {
                if (manager.isDebug()) e.printStackTrace();
                manager.consoleKey("extractReflectError", method.getName());
            }
        }
        command.perm = perm;
        command.onlyPlayer = sub.onlyPlayer();
        command.aliases = new ArrayList<>(Arrays.asList(sub.aliases()));
        command.setTabCompletions(sub.tabs());
        command.usage = sub.usage();
        command.update();
    }

    // 返回的是最终命令
    private SpongeCommand createSub(Paths paths) {
        if (paths.empty()) return this;
        SpongeCommand sub = subs.get(paths.first());
        if (sub == null) sub = new SpongeCommand(this, paths.first(), null, false, manager);
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
    public void execute(CommandSource sender, Paths args) {
        if (executor != null) executor.execute(this, sender, args);
        else if (args.notEmpty()) {
            SpongeCommand sub = subs.get(args.first());
            if (sub != null) {
                if (sub.testPermission(sender)) {
                    args.next();
                    if (sender instanceof Player) sub.execute((Player) sender, args);
                    else if (!sub.onlyPlayer) sub.execute(sender, args);
                    else manager.sendKey(sender, "onlyPlayer");
                } else manager.sendKey(sender, "noCommandPerm", sub.perm);
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
        execute((CommandSource) player, args);
    }

    @Nonnull
    public CommandResult process(@Nonnull CommandSource sender, @Nonnull String args) {
        if (testPermission(sender)) {
            if (sender instanceof Player) execute(((Player) sender), new Paths(true, args));
            else if (!onlyPlayer) execute(sender, new Paths(true, args));
            else manager.sendKey(sender, "onlyPlayer");
        } else manager.sendKey(sender, "noCommandPerm", perm);
        return CommandResult.success();
    }

    /**
     * 发送使用方法.
     *
     * @param sender 信息接收者
     */
    public void sendUsage(CommandSource sender) {
        if (usage != null && !usage.isEmpty()) {
            manager.sendKey(sender, "cmdUsage", usage);
        }
    }

    @Nonnull
    public Text getUsage(@Nonnull CommandSource source) {
        return Text.of(usage == null ? "" : usage);
    }

    /**
     * 设置用法.
     *
     * @param usage 用法
     */
    public void setUsage(String usage) {
        this.usage = usage;
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

    @Nonnull
    public List<String> getSuggestions(@Nonnull CommandSource sender, @Nonnull String args, @Nullable Location<World> location) {
        return tabCompletions(new Paths(true, args));
    }

    /**
     * 命令发送者是否能执行此命令.
     *
     * @param source 命令发送者
     * @return 是否能执行此命令
     */
    public boolean testPermission(@Nonnull CommandSource source) {
        return perm == null || source.hasPermission(perm);
    }

    @Nonnull
    public Optional<Text> getShortDescription(@Nonnull CommandSource source) {
        return Optional.of(getUsage(source));
    }

    @Nonnull
    public Optional<Text> getHelp(@Nonnull CommandSource source) {
        return Optional.of(getUsage(source));
    }

    /**
     * 获取命令别名列表.
     *
     * @return 命令别名
     */
    public List<String> getAliases() {
        return aliases;
    }
}
