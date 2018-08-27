package org.soraworld.violet.command;

import org.soraworld.violet.Violet;
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

import static org.soraworld.violet.Violet.KEY_CMD_USAGE;

/**
 * Sponge 命令.
 */
public class SpongeCommand implements CommandCallable {

    protected final String name;
    /**
     * 权限.
     */
    protected String perm;
    /**
     * 是否仅玩家执行.
     */
    protected boolean onlyPlayer;
    /**
     * 管理器.
     */
    protected final SpongeManager manager;
    protected ArrayList<String> tabs;

    protected SpongeExecutor executor;
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();
    private static final MethodType INVOKE_EXEC = MethodType.methodType(SpongeExecutor.class);

    /**
     * 别名列表.
     */
    protected List<String> aliases = new ArrayList<>();
    /**
     * 子命令.
     */
    protected final HashMap<String, SpongeCommand> subs = new LinkedHashMap<>();
    private String usage;

    /**
     * 实例化 Sponge 命令.
     *
     * @param name       命令主名
     * @param perm       权限
     * @param onlyPlayer 是否仅玩家执行
     * @param manager    管理器
     * @param aliases    别名
     */
    public SpongeCommand(@Nonnull String name, @Nullable String perm, boolean onlyPlayer, @Nonnull SpongeManager manager, String... aliases) {
        this.name = name;
        this.perm = perm;
        this.manager = manager;
        this.onlyPlayer = onlyPlayer;
        this.aliases.add(name);
        this.aliases.addAll(Arrays.asList(aliases));
        this.aliases.removeIf(s -> s == null || s.isEmpty() || s.contains(" ") || s.contains(":"));
    }

    /**
     * 执行命令.
     *
     * @param sender 命令发送者
     * @param args   参数
     */
    public void execute(CommandSource sender, CommandArgs args) {
        if (executor != null) executor.execute(manager, sender, args);
        else if (args.notEmpty()) {
            SpongeCommand sub = subs.get(args.first());
            if (sub != null) {
                if (sub.testPermission(sender)) {
                    args.next();
                    if (sender instanceof Player) sub.execute((Player) sender, args);
                    else if (!sub.onlyPlayer) sub.execute(sender, args);
                    else manager.sendKey(sender, Violet.KEY_ONLY_PLAYER);
                } else manager.sendKey(sender, Violet.KEY_NO_CMD_PERM, sub.perm);
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
        execute((CommandSource) player, args);
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

    private void setTabCompletions(String[] tabs) {
        if (tabs != null && tabs.length > 0) {
            this.tabs = new ArrayList<>(Arrays.asList(tabs));
        } else this.tabs = null;
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
            Method theMethod = clazz.getDeclaredMethod(method, SpongeManager.class, CommandSource.class, CommandArgs.class);
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
        if (params.length != 3 || params[0] != SpongeManager.class || params[1] != CommandSource.class || params[2] != CommandArgs.class) return;
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
            SpongeExecutor executor = (SpongeExecutor) site.getTarget().invokeExact();
            Paths paths = new Paths(sub.paths());
            String name = paths.empty() ? method.getName().toLowerCase() : paths.first().replace(' ', '_').replace(':', '_');
            if (paths.empty()) paths = new Paths(name);
            else paths.set(0, name);
            String perm = sub.perm().isEmpty() ? null : sub.perm().replace(' ', '_').replace(':', '_');
            if ("admin".equals(perm)) perm = manager.defAdminPerm();
            SpongeCommand command = getOrCreateSub(paths);
            command.executor = executor;
            command.perm = perm;
            command.onlyPlayer = sub.onlyPlayer();
            command.aliases = new ArrayList<>(Arrays.asList(sub.aliases()));
            command.setTabCompletions(sub.tabs());
            command.setUsage(sub.usage());
            addSub(command);
        } catch (Throwable e) {
            if (manager.isDebug()) e.printStackTrace();
            manager.consoleKey("extractReflectError", method.getName());
        }
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    private SpongeCommand getOrCreateSub(Paths paths) {
        if (paths.empty()) return this;
        SpongeCommand sub = subs.get(paths.first());
        if (sub == null) {
            sub = new SpongeCommand(paths.first(), null, false, manager);
            addSub(sub);
        }
        return sub.getOrCreateSub(paths.next());
    }

    /**
     * 发送使用方法.
     *
     * @param sender 信息接收者
     */
    protected void sendUsage(CommandSource sender) {
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

    @Nonnull
    public CommandResult process(@Nonnull CommandSource sender, @Nonnull String args) {
        if (testPermission(sender)) {
            if (sender instanceof Player) execute(((Player) sender), new CommandArgs(args));
            else if (!onlyPlayer) execute(sender, new CommandArgs(args));
            else manager.sendKey(sender, Violet.KEY_ONLY_PLAYER);
        } else manager.sendKey(sender, Violet.KEY_NO_CMD_PERM, perm);
        return CommandResult.success();
    }

    @Nonnull
    public List<String> getSuggestions(@Nonnull CommandSource sender, @Nonnull String args, @Nullable Location<World> location) {
        return tabCompletions(new CommandArgs(args));
    }

    /**
     * 命令发送者是否能执行此命令.
     *
     * @param source 命令发送者
     * @return 是否能执行此命令
     */
    public final boolean testPermission(@Nonnull CommandSource source) {
        return perm == null || source.hasPermission(perm);
    }

    @Nonnull
    public final Optional<Text> getShortDescription(@Nonnull CommandSource source) {
        return Optional.of(getUsage(source));
    }

    @Nonnull
    public final Optional<Text> getHelp(@Nonnull CommandSource source) {
        return Optional.of(getUsage(source));
    }

    @Nonnull
    public final Text getUsage(@Nonnull CommandSource source) {
        return Text.of(usage == null ? "" : usage);
    }

    public List<String> getAliases() {
        return aliases;
    }
}
