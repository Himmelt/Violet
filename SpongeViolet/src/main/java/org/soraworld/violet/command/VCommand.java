package org.soraworld.violet.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.hocon.node.Paths;
import org.soraworld.hocon.util.Reflects;
import org.soraworld.violet.manager.VManager;
import org.soraworld.violet.util.ListUtils;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

public class VCommand implements CommandCallable {

    protected String name;
    protected String usageMessage;
    protected String permission;
    protected boolean onlyPlayer;
    protected VCommand parent;
    protected VManager manager;
    protected SubExecutor<CommandSource> subExecutor;
    protected TabExecutor<CommandSource> tabExecutor;
    protected final List<String> tabs = new ArrayList<>();
    protected final List<String> aliases = new ArrayList<>();
    protected final Map<String, VCommand> subs = new LinkedHashMap<>();

    public VCommand(@NotNull String name, @Nullable String permission, boolean onlyPlayer, @Nullable VCommand parent, @NotNull VManager manager) {
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

        SubExecutor<CommandSource> executor = null;
        try {
            executor = (SubExecutor) field.get(instance);
        } catch (Throwable e) {
            if (manager.isDebug()) e.printStackTrace();
        }
        if (executor == null) return;

        Type[] params = Reflects.getActualTypes(SubExecutor.class, executor.getClass());
        if (params == null || params.length != 1 || !Reflects.isAssignableFrom(CommandSource.class, params[0])) return;

        Paths paths = new Paths(sub.path().isEmpty() ? field.getName().toLowerCase() : sub.path().replace(' ', '_').replace(':', '_'));
        String perm = sub.perm().isEmpty() ? null : sub.perm().replace(' ', '_').replace(':', '_');
        if ("admin".equals(perm)) perm = manager.defAdminPerm();
        VCommand command = createSub(paths);
        if (!sub.virtual()) command.subExecutor = executor;
        command.permission = perm;
        command.onlyPlayer = sub.onlyPlayer() || Reflects.isAssignableFrom(Player.class, params[0]);
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

        TabExecutor<CommandSource> executor = null;
        try {
            executor = (TabExecutor) field.get(instance);
        } catch (Throwable e) {
            if (manager.isDebug()) e.printStackTrace();
        }
        if (executor == null) return;

        Type[] params = Reflects.getActualTypes(TabExecutor.class, executor.getClass());
        if (params == null || params.length != 1 || !Reflects.isAssignableFrom(CommandSource.class, params[0])) return;

        Paths paths = new Paths(tab.path().isEmpty() ? field.getName().toLowerCase() : tab.path().replace(' ', '_').replace(':', '_'));
        VCommand command = getSub(paths);
        if (command != null) command.tabExecutor = executor;
    }

    public void sendUsage(CommandSource sender) {
        if (usageMessage != null && !usageMessage.isEmpty()) {
            manager.sendKey(sender, "cmdUsage", usageMessage);
        }
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

    /* ---------------------------------------- modify start -------------------------------------------- */

    public void execute(CommandSource sender, Args args) {
        if (subExecutor == null) {
            if (args.notEmpty()) {
                VCommand sub = subs.get(args.first());
                if (sub != null) {
                    sub.execute(sender, args.next());
                    return;
                }
            }
            sendUsage(sender);
        } else subExecutor.execute(this, sender, args);
    }

    public List<String> tabComplete(CommandSource sender, Args args) {
        if (tabExecutor != null) return tabExecutor.complete(this, sender, args);
        String first = args.first();
        if (args.size() == 1) {
            return ListUtils.getMatchList(first, !tabs.isEmpty() ? tabs : subs.keySet());
        }
        if (subs.containsKey(first)) {
            args.next();
            return subs.get(first).tabComplete(sender, args);
        }
        return new ArrayList<>();
    }

    /* ---------------------------------------- origin start -------------------------------------------- */

    public String getName() {
        return name;
    }

    public VCommand setAliases(List<String> aliases) {
        this.aliases.clear();
        this.aliases.addAll(aliases);
        return this;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setUsage(String usage) {
        this.usageMessage = usage;
    }

    @NotNull
    public CommandResult process(@NotNull CommandSource sender, @NotNull String args) {
        if (testPermission(sender)) {
            if (!onlyPlayer || sender instanceof Player) {
                execute(sender, new Args(args));
            } else manager.sendKey(sender, "onlyPlayer");
        } else manager.sendKey(sender, "noCommandPerm", permission);
        return CommandResult.success();
    }

    @NotNull
    public List<String> getSuggestions(@NotNull CommandSource sender, @NotNull String args, Location<World> location) {
        String[] ss = args.trim().split("[ ]+");
        if (!args.isEmpty() && args.endsWith(" ")) {
            ss = Arrays.copyOf(ss, ss.length + 1);
            ss[ss.length - 1] = "";
        }
        return tabComplete(sender, new Args(ss));
    }

    public boolean testPermission(@NotNull CommandSource sender) {
        return permission == null || sender.hasPermission(permission);
    }

    @NotNull
    public Text getUsage(@NotNull CommandSource sender) {
        return Text.of(usageMessage);
    }

    @NotNull
    public Optional<Text> getShortDescription(@NotNull CommandSource source) {
        return Optional.of(getUsage(source));
    }

    @NotNull
    public Optional<Text> getHelp(@NotNull CommandSource source) {
        return Optional.of(getUsage(source));
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof VCommand) return name.equals(((VCommand) obj).name);
        return false;
    }
}
