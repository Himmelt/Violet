package org.soraworld.violet.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.hocon.node.Paths;
import org.soraworld.hocon.util.Reflects;
import org.soraworld.violet.api.ICommandSender;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.api.IPlayer;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.util.ListUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type V command.
 *
 * @author Himmelt
 */
public final class CommandCore {

    private String exePermission = null;
    private boolean exeOnlyPlayer = false;
    private boolean tabOnlyPlayer = false;

    /**
     * The Name.
     */
    protected String name;
    /**
     * The Permission.
     */
    protected String permission;
    /**
     * The Only player.
     */
    protected boolean onlyPlayer;
    /**
     * The Parent.
     */
    protected CommandCore parent;
    /**
     * The Sub executor.
     */
    protected SubExecutor<ICommandSender> subExecutor;
    /**
     * The Tab executor.
     */
    protected TabExecutor<ICommandSender> tabExecutor;
    /**
     * The Tabs.
     */
    protected final List<String> tabs = new ArrayList<>();
    /**
     * The Aliases.
     */
    protected final List<String> aliases = new ArrayList<>();
    /**
     * The Subs.
     */
    protected final Map<String, CommandCore> subs = new LinkedHashMap<>();
    private String usage;

    @Inject
    private IManager manager;
    @Inject
    private IPlugin plugin;

    /**
     * Instantiates a new V command.
     *
     * @param name       the name
     * @param permission the permission
     * @param onlyPlayer the only player
     * @param parent     the parent
     * @param manager    the manager
     */
    public CommandCore(@NotNull String name, @Nullable String permission, boolean onlyPlayer, @Nullable CommandCore parent, @NotNull IManager manager) {
        this.name = name;
        this.permission = permission;
        this.onlyPlayer = onlyPlayer;
        this.parent = parent;
        this.manager = manager;
    }

    /**
     * Add sub.
     *
     * @param sub the sub
     */
    public void addSub(@NotNull CommandCore sub) {
        CommandCore old = subs.get(sub.getName());
        if (old != null) {
            subs.entrySet().removeIf(entry -> entry.getValue() == old);
            if (old != sub) {
                old.subs.forEach(sub.subs::putIfAbsent);
            }
        }
        subs.put(sub.getName(), sub);
        for (String alias : sub.getAliases()) {
            subs.putIfAbsent(alias, sub);
        }
    }

    private CommandCore createSub(Paths paths) {
        if (paths.empty()) {
            return this;
        }
        CommandCore sub = subs.get(paths.first());
        if (sub == null) {
            sub = new CommandCore(paths.first(), null, false, this, manager);
        }
        return sub.createSub(paths.next());
    }

    private void update() {
        if (parent != null) {
            parent.addSub(this);
            parent.update();
        }
    }

    /**
     * Extract sub.
     *
     * @param instance the instance
     */
    public void extractSub(@NotNull Object instance) {
        Field[] fields = instance.getClass().getDeclaredFields();
        if (fields.length == 0) {
            return;
        }
        for (Field field : fields) {
            tryAddSub(field, instance);
        }
    }

    /**
     * Extract sub.
     *
     * @param instance the instance
     * @param name     the name
     */
    public void extractSub(@NotNull Object instance, @NotNull String name) {
        if (name.isEmpty()) {
            return;
        }
        try {
            Field field = instance.getClass().getDeclaredField(name);
            tryAddSub(field, instance);
        } catch (Throwable e) {
            manager.debug(e);
            manager.consoleKey("extractNoSuchSub", instance.getClass().getName(), name);
        }
    }

    private void tryAddSub(@NotNull Field field, @NotNull Object instance) {
        Sub sub = field.getAnnotation(Sub.class);
        if (sub == null || Modifier.isStatic(field.getModifiers())) {
            return;
        }
        if (!sub.parent().isEmpty() && !sub.parent().equalsIgnoreCase(getName())) {
            return;
        }
        Paths paths = new Paths(sub.path().isEmpty() ? field.getName().toLowerCase() : sub.path().replace(' ', '_').replace(':', '_'));
        String perm = sub.perm().isEmpty() ? null : sub.perm().replace(' ', '_').replace(':', '_');
        perm = manager.mappingPerm(perm);

        CommandCore command;
        if (!sub.virtual()) {
            SubExecutor<ICommandSender> executor = null;
            try {
                executor = (SubExecutor) field.get(instance);
            } catch (Throwable e) {
                manager.debug(e);
            }
            if (executor == null) {
                return;
            }
            Type[] params = Reflects.getActualTypes(SubExecutor.class, executor.getClass());
            if (params == null || params.length != 1 || !Reflects.isAssignableFrom(ICommandSender.class, params[0])) {
                return;
            }

            if (".".equals(sub.path())) {
                this.subExecutor = executor;
                this.exePermission = perm;
                this.exeOnlyPlayer = sub.onlyPlayer() || Reflects.isAssignableFrom(IPlayer.class, params[0]);
                return;
            }
            command = createSub(paths);
            command.subExecutor = executor;
            command.onlyPlayer = command.onlyPlayer || sub.onlyPlayer() || Reflects.isAssignableFrom(IPlayer.class, params[0]);
        } else {
            command = createSub(paths);
            command.onlyPlayer = command.onlyPlayer || sub.onlyPlayer();
        }
        command.permission = perm;
        command.tabs.addAll(Arrays.asList(sub.tabs()));
        command.aliases.addAll(Arrays.asList(sub.aliases()));
        command.usage = sub.usage();
        command.update();
    }

    /**
     * Extract tab.
     *
     * @param instance the instance
     */
    public void extractTab(@NotNull Object instance) {
        Field[] fields = instance.getClass().getDeclaredFields();
        if (fields.length == 0) {
            return;
        }
        for (Field field : fields) {
            tryAddTab(field, instance);
        }
    }

    /**
     * Extract tab.
     *
     * @param instance the instance
     * @param name     the name
     */
    public void extractTab(@NotNull Object instance, @NotNull String name) {
        if (name.isEmpty()) {
            return;
        }
        try {
            Field field = instance.getClass().getDeclaredField(name);
            tryAddTab(field, instance);
        } catch (Throwable e) {
            manager.debug(e);
            manager.consoleKey("extractNoSuchTab", instance.getClass().getName(), name);
        }
    }

    private void tryAddTab(@NotNull Field field, @NotNull Object instance) {
        Tab tab = field.getAnnotation(Tab.class);
        if (tab == null || Modifier.isStatic(field.getModifiers())) {
            return;
        }
        if (!tab.parent().isEmpty() && !tab.parent().equalsIgnoreCase(getName())) {
            return;
        }

        TabExecutor<ICommandSender> executor = null;
        try {
            executor = (TabExecutor) field.get(instance);
        } catch (Throwable e) {
            manager.debug(e);
        }
        if (executor == null) {
            return;
        }

        Type[] params = Reflects.getActualTypes(TabExecutor.class, executor.getClass());
        if (params == null || params.length != 1 || !Reflects.isAssignableFrom(ICommandSender.class, params[0])) {
            return;
        }

        if (".".equals(tab.path())) {
            this.tabExecutor = executor;
            this.tabOnlyPlayer = Reflects.isAssignableFrom(IPlayer.class, params[0]);
            return;
        }

        Paths paths = new Paths(tab.path().isEmpty() ? field.getName().toLowerCase() : tab.path().replace(' ', '_').replace(':', '_'));
        CommandCore command = getSub(paths);
        if (command != null) {
            command.tabExecutor = executor;
            command.tabOnlyPlayer = Reflects.isAssignableFrom(IPlayer.class, params[0]);
        }
    }

    /**
     * Send usage.
     *
     * @param sender the sender
     */
    public void sendUsage(ICommandSender sender) {
        manager.sendKey(sender, "cmdUsage", getUsage());
    }

    /**
     * Gets sub.
     *
     * @param name the name
     * @return the sub
     */
    public CommandCore getSub(String name) {
        return subs.get(name);
    }

    /**
     * Gets sub.
     *
     * @param paths the paths
     * @return the sub
     */
    public CommandCore getSub(Paths paths) {
        if (paths.empty()) {
            return this;
        }
        CommandCore sub = subs.get(paths.first());
        if (sub != null) {
            return sub.getSub(paths.next());
        }
        return null;
    }

    /**
     * Gets parent.
     *
     * @return the parent
     */
    public CommandCore getParent() {
        return parent;
    }

    /**
     * Sets tabs.
     *
     * @param tabs the tabs
     */
    public void setTabs(List<String> tabs) {
        this.tabs.clear();
        this.tabs.addAll(tabs);
    }

    /**
     * Gets tabs.
     *
     * @return the tabs
     */
    public List<String> getTabs() {
        return new ArrayList<>(tabs);
    }

    /* ---------------------------------------- modify start -------------------------------------------- */

    /**
     * Execute.
     * 执行器非空时，参数优先为子命令
     *
     * @param sender the sender
     * @param args   the args
     */
    public void execute(ICommandSender sender, Args args) {
        if (testPermission(sender)) {
            if (!onlyPlayer || sender instanceof IPlayer) {
                if (args.notEmpty()) {
                    CommandCore sub = subs.get(args.first());
                    if (sub != null) {
                        sub.execute(sender, args.next());
                        return;
                    }
                }
                if (subExecutor != null) {
                    if (exePermission == null || exePermission.isEmpty() || sender.hasPermission(exePermission)) {
                        if (!exeOnlyPlayer || sender instanceof IPlayer) {
                            subExecutor.execute(this, sender, args);
                        } else {
                            manager.sendKey(sender, "onlyPlayer");
                        }
                    } else {
                        manager.sendKey(sender, "noCommandPerm", exePermission);
                    }
                } else {
                    sendUsage(sender);
                }
            } else {
                manager.sendKey(sender, "onlyPlayer");
            }
        } else {
            manager.sendKey(sender, "noCommandPerm", permission);
        }
    }

    /**
     * Tab complete list.
     *
     * @param sender the sender
     * @param args   the args
     * @return the list
     */
    public List<String> tabComplete(ICommandSender sender, Args args) {
        return tabComplete(sender, args, false);
    }

    /**
     * Tab complete list.
     *
     * @param sender       the sender
     * @param args         the args
     * @param skipExecutor the skip executor
     * @return the list
     */
    public List<String> tabComplete(ICommandSender sender, Args args, boolean skipExecutor) {
        if (!skipExecutor && tabExecutor != null && (!tabOnlyPlayer || sender instanceof IPlayer)) {
            return tabExecutor.complete(this, sender, args);
        }
        String first = args.first();
        if (args.size() == 1) {
            return ListUtils.getMatchList(first, !tabs.isEmpty() ? tabs : subs.keySet().stream().filter(s -> subs.get(s).testPermission(sender)).collect(Collectors.toList()));
        }
        if (subs.containsKey(first) && subs.get(first).testPermission(sender)) {
            args.next();
            return subs.get(first).tabComplete(sender, args);
        }
        return new ArrayList<>();
    }

    /* ---------------------------------------- origin start -------------------------------------------- */

    public @NotNull String getName() {
        return name;
    }

    public @NotNull String getUsage() {
        if (usage == null || usage.isEmpty()) {
            StringBuilder builder = new StringBuilder(getName());
            CommandCore parent = this.parent;
            while (parent != null) {
                builder.insert(0, parent.getName() + " ");
                parent = parent.parent;
            }
            builder.insert(0, "/");
            usage = builder.toString();
        }
        return manager.trans(usage).replace("{$id}", plugin.getId());
    }

    public @NotNull CommandCore setAliases(@NotNull List<String> aliases) {
        this.aliases.clear();
        this.aliases.addAll(aliases);
        return this;
    }

    public @NotNull List<String> getAliases() {
        return aliases;
    }


    public boolean testPermission(@NotNull ICommandSender sender) {
        return permission == null || permission.isEmpty() || sender.hasPermission(permission);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CommandCore && name.equals(((CommandCore) obj).name);
    }

    public Collection<CommandCore> getSubs() {
        return subs.values();
    }

    public Collection<String> getSubKeys() {
        return subs.keySet();
    }
}
