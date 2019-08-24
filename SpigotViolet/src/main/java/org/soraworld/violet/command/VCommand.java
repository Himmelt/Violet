package org.soraworld.violet.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.hocon.node.Paths;
import org.soraworld.hocon.util.Reflects;
import org.soraworld.violet.manager.VManager;
import org.soraworld.violet.util.ListUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class VCommand extends Command {

    private String exePermission = null;
    private boolean exeOnlyPlayer = false;
    private boolean tabOnlyPlayer = false;

    protected String name;
    protected String permission;
    protected boolean onlyPlayer;
    protected VCommand parent;
    protected VManager manager;
    protected SubExecutor<CommandSender> subExecutor;
    protected TabExecutor<CommandSender> tabExecutor;
    protected final List<String> tabs = new ArrayList<>();
    protected final List<String> aliases = new ArrayList<>();
    protected final Map<String, VCommand> subs = new LinkedHashMap<>();

    public VCommand(@NotNull String name, @Nullable String permission, boolean onlyPlayer, @Nullable VCommand parent, @NotNull VManager manager) {
        super(name);
        this.name = name;
        this.permission = permission;
        this.onlyPlayer = onlyPlayer;
        this.parent = parent;
        this.manager = manager;
    }

    public void addSub(@NotNull VCommand sub) {
        VCommand old = subs.get(sub.getName());
        if (old != null) {
            subs.entrySet().removeIf(entry -> entry.getValue() == old);
            if (old != sub) old.subs.forEach(sub.subs::putIfAbsent);
        }
        subs.put(sub.getName(), sub);
        for (String alias : sub.getAliases()) {
            subs.putIfAbsent(alias, sub);
        }
    }

    private VCommand createSub(Paths paths) {
        if (paths.empty()) return this;
        VCommand sub = subs.get(paths.first());
        if (sub == null) sub = new VCommand(paths.first(), null, false, this, manager);
        return sub.createSub(paths.next());
    }

    private void update() {
        if (parent != null) {
            parent.addSub(this);
            parent.update();
        }
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
            if (manager.isDebug()) e.printStackTrace();
            manager.consoleKey("extractNoSuchSub", instance.getClass().getName(), name);
        }
    }

    private void tryAddSub(@NotNull Field field, @NotNull Object instance) {
        Sub sub = field.getAnnotation(Sub.class);
        if (sub == null || Modifier.isStatic(field.getModifiers())) return;
        if (!sub.parent().isEmpty() && !sub.parent().equalsIgnoreCase(getName())) return;
        Paths paths = new Paths(sub.path().isEmpty() ? field.getName().toLowerCase() : sub.path().replace(' ', '_').replace(':', '_'));
        String perm = sub.perm().isEmpty() ? null : sub.perm().replace(' ', '_').replace(':', '_');
        perm = manager.mappingPerm(perm);

        VCommand command;
        if (!sub.virtual()) {
            SubExecutor<CommandSender> executor = null;
            try {
                executor = (SubExecutor) field.get(instance);
            } catch (Throwable e) {
                if (manager.isDebug()) e.printStackTrace();
            }
            if (executor == null) return;
            Type[] params = Reflects.getActualTypes(SubExecutor.class, executor.getClass());
            if (params == null || params.length != 1 || !Reflects.isAssignableFrom(CommandSender.class, params[0])) return;

            if (sub.path().equals(".")) {
                this.subExecutor = executor;
                this.exePermission = perm;
                this.exeOnlyPlayer = sub.onlyPlayer() || Reflects.isAssignableFrom(Player.class, params[0]);
                return;
            }
            command = createSub(paths);
            command.subExecutor = executor;
            command.onlyPlayer = command.onlyPlayer || sub.onlyPlayer() || Reflects.isAssignableFrom(Player.class, params[0]);
        } else {
            command = createSub(paths);
            command.onlyPlayer = command.onlyPlayer || sub.onlyPlayer();
        }
        command.permission = perm;
        command.tabs.addAll(Arrays.asList(sub.tabs()));
        command.aliases.addAll(Arrays.asList(sub.aliases()));
        command.usageMessage = sub.usage();
        command.update();
    }

    public void extractTab(@NotNull Object instance) {
        Field[] fields = instance.getClass().getDeclaredFields();
        if (fields == null || fields.length == 0) return;
        for (Field field : fields) tryAddTab(field, instance);
    }

    public void extractTab(@NotNull Object instance, @NotNull String name) {
        if (name.isEmpty()) return;
        try {
            Field field = instance.getClass().getDeclaredField(name);
            tryAddTab(field, instance);
        } catch (Throwable e) {
            if (manager.isDebug()) e.printStackTrace();
            manager.consoleKey("extractNoSuchTab", instance.getClass().getName(), name);
        }
    }

    private void tryAddTab(@NotNull Field field, @NotNull Object instance) {
        Tab tab = field.getAnnotation(Tab.class);
        if (tab == null || Modifier.isStatic(field.getModifiers())) return;
        if (!tab.parent().isEmpty() && !tab.parent().equalsIgnoreCase(getName())) return;

        TabExecutor<CommandSender> executor = null;
        try {
            executor = (TabExecutor) field.get(instance);
        } catch (Throwable e) {
            if (manager.isDebug()) e.printStackTrace();
        }
        if (executor == null) return;

        Type[] params = Reflects.getActualTypes(TabExecutor.class, executor.getClass());
        if (params == null || params.length != 1 || !Reflects.isAssignableFrom(CommandSender.class, params[0])) return;

        if (tab.path().equals(".")) {
            this.tabExecutor = executor;
            this.tabOnlyPlayer = Reflects.isAssignableFrom(Player.class, params[0]);
            return;
        }

        Paths paths = new Paths(tab.path().isEmpty() ? field.getName().toLowerCase() : tab.path().replace(' ', '_').replace(':', '_'));
        VCommand command = getSub(paths);
        if (command != null) {
            command.tabExecutor = executor;
            command.tabOnlyPlayer = Reflects.isAssignableFrom(Player.class, params[0]);
        }
    }

    public void sendUsage(CommandSender sender) {
        manager.sendKey(sender, "cmdUsage", getUsage());
    }

    public VCommand getSub(String name) {
        return subs.get(name);
    }

    public VCommand getSub(Paths paths) {
        if (paths.empty()) return this;
        VCommand sub = subs.get(paths.first());
        if (sub != null) return sub.getSub(paths.next());
        return null;
    }

    public VCommand getParent() {
        return parent;
    }

    public void setTabs(List<String> tabs) {
        this.tabs.clear();
        this.tabs.addAll(tabs);
    }

    public List<String> getTabs() {
        return new ArrayList<>(tabs);
    }

    /* ---------------------------------------- modify start -------------------------------------------- */

    /* 执行器非空时，参数优先为子命令 */
    public void execute(CommandSender sender, Args args) {
        if (testPermission(sender)) {
            if (!onlyPlayer || sender instanceof Player) {
                if (args.notEmpty()) {
                    VCommand sub = subs.get(args.first());
                    if (sub != null) {
                        sub.execute(sender, args.next());
                        return;
                    }
                }
                if (subExecutor != null) {
                    if (exePermission == null || exePermission.isEmpty() || sender.hasPermission(exePermission)) {
                        if (!exeOnlyPlayer || sender instanceof Player) {
                            subExecutor.execute(this, sender, args);
                        } else manager.sendKey(sender, "onlyPlayer");
                    } else manager.sendKey(sender, "noCommandPerm", exePermission);
                } else sendUsage(sender);
            } else manager.sendKey(sender, "onlyPlayer");
        } else manager.sendKey(sender, "noCommandPerm", permission);
    }

    public List<String> tabComplete(CommandSender sender, Args args) {
        return tabComplete(sender, args, false);
    }

    public List<String> tabComplete(CommandSender sender, Args args, boolean skipExecutor) {
        if (!skipExecutor && tabExecutor != null && (!tabOnlyPlayer || sender instanceof Player)) {
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return getUsage();
    }

    public String getUsage() {
        if (usageMessage == null || usageMessage.isEmpty()) {
            StringBuilder builder = new StringBuilder(getName());
            VCommand parent = this.parent;
            while (parent != null) {
                builder.insert(0, parent.getName() + " ");
                parent = parent.parent;
            }
            builder.insert(0, "/");
            usageMessage = builder.toString();
        }
        return manager.trans(usageMessage).replace("{$id}", manager.getPlugin().getId());
    }

    public VCommand setAliases(List<String> aliases) {
        this.aliases.clear();
        this.aliases.addAll(aliases);
        return this;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        execute(sender, new Args(args));
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return tabComplete(sender, new Args(args));
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) {
        return tabComplete(sender, new Args(args));
    }

    public boolean testPermission(CommandSender sender) {
        return permission == null || permission.isEmpty() || sender.hasPermission(permission);
    }

    public boolean testPermissionSilent(CommandSender sender) {
        return permission == null || permission.isEmpty() || sender.hasPermission(permission);
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof VCommand) return name.equals(((VCommand) obj).name);
        return false;
    }
}
