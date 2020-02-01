package org.soraworld.violet.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.hocon.node.Paths;
import org.soraworld.hocon.util.Reflects;
import org.soraworld.violet.api.ICommandSender;
import org.soraworld.violet.api.IPlayer;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.inject.Cmd;
import org.soraworld.violet.inject.Tab;
import org.soraworld.violet.util.ListUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Himmelt
 */
public final class CommandCore {

    private String perm = null;
    private String usage = "";
    private String description = "";
    private boolean ingame = false;
    private SubExecutor<ICommandSender> executor;
    private TabExecutor<ICommandSender> tabcutor;

    private final String name;
    private final IPlugin plugin;
    private final CommandCore parent;
    private final List<String> tabs = new ArrayList<>();
    private final List<String> aliases = new ArrayList<>();
    private final Map<String, CommandCore> subs = new LinkedHashMap<>();

    private CommandCore(@NotNull IPlugin plugin, @NotNull String name, @Nullable CommandCore parent) {
        this.name = name;
        this.parent = parent;
        this.plugin = plugin;
    }

    public CommandCore(@NotNull IPlugin plugin, @NotNull Cmd cmd) {
        this.name = cmd.name().toLowerCase();
        this.perm = cmd.admin() ? plugin.getId() + ".admin" : cmd.perm().isEmpty() ? null : cmd.perm();
        this.ingame = cmd.ingame();
        this.tabs.addAll(Arrays.asList(cmd.tabs()));
        this.aliases.addAll(Arrays.asList(cmd.aliases()));
        this.aliases.removeIf(name::equalsIgnoreCase);
        this.parent = null;
        this.plugin = plugin;
        this.usage = cmd.usage();
        this.description = cmd.description();
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull String getUsage() {
        return plugin.trans(usage).replace("{$id}", plugin.getId());
    }

    public @NotNull String getDescription() {
        return plugin.trans(description);
    }

    public @NotNull List<String> getAliases() {
        return aliases;
    }

    public void addSub(@NotNull CommandCore sub) {
        CommandCore old = subs.get(sub.getName());
        if (old != sub) {
            if (old != null) {
                subs.entrySet().removeIf(entry -> entry.getValue() == old);
                old.subs.forEach(sub.subs::putIfAbsent);
            }
            subs.put(sub.getName(), sub);
        }
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
            sub = new CommandCore(plugin, paths.first(), this);
        }
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
        if (fields.length == 0) {
            return;
        }
        for (Field field : fields) {
            tryAddSub(field, instance);
        }
    }

    public void extractSub(@NotNull Object instance, @NotNull String name) {
        if (name.isEmpty()) {
            return;
        }
        try {
            Field field = instance.getClass().getDeclaredField(name);
            tryAddSub(field, instance);
        } catch (Throwable e) {
            plugin.debug(e);
            plugin.consoleKey("extractNoSuchSub", instance.getClass().getName(), name);
        }
    }

    private void tryAddSub(@NotNull Field field, @NotNull Object instance) {
        Cmd sub = field.getAnnotation(Cmd.class);
        if (sub == null || Modifier.isStatic(field.getModifiers())) {
            return;
        }
        if (!sub.plugin().isEmpty() && !sub.plugin().equalsIgnoreCase(plugin.getId())) {
            return;
        }
        String perm = sub.admin() ? plugin.getId() + ".admin" : sub.perm().isEmpty() ? null : sub.perm();

        SubExecutor<ICommandSender> executor = null;
        try {
            executor = (SubExecutor) field.get(instance);
        } catch (Throwable e) {
            plugin.debug(e);
        }
        if (executor == null) {
            return;
        }
        Type[] params = Reflects.getActualTypes(SubExecutor.class, executor.getClass());
        if (params == null || params.length != 1 || !Reflects.isAssignableFrom(ICommandSender.class, params[0])) {
            return;
        }

        if (".".equals(sub.name())) {
            this.perm = perm;
            this.usage = sub.usage().isEmpty() ? "usage." + name : sub.usage();
            this.ingame = sub.ingame() || Reflects.isAssignableFrom(IPlayer.class, params[0]);
            this.description = sub.description().isEmpty() ? "description." + name : sub.description();
            this.executor = executor;
            this.tabs.addAll(Arrays.asList(sub.tabs()));
        } else {
            CommandCore cmd = createSub(new Paths(sub.name().isEmpty() ? field.getName().toLowerCase() : sub.name().toLowerCase()));
            cmd.perm = perm;
            cmd.usage = sub.usage().isEmpty() ? "usage." + cmd.name : sub.usage();
            cmd.ingame = sub.ingame() || Reflects.isAssignableFrom(IPlayer.class, params[0]);
            cmd.description = sub.description().isEmpty() ? "description." + cmd.name : sub.description();
            cmd.executor = executor;
            cmd.tabs.addAll(Arrays.asList(sub.tabs()));
            cmd.aliases.addAll(Arrays.asList(sub.aliases()));
            cmd.aliases.removeIf(cmd.name::equalsIgnoreCase);
            cmd.update();
        }
    }

    public void extractTab(@NotNull Object instance) {
        Field[] fields = instance.getClass().getDeclaredFields();
        if (fields.length == 0) {
            return;
        }
        for (Field field : fields) {
            tryAddTab(field, instance);
        }
    }

    public void extractTab(@NotNull Object instance, @NotNull String name) {
        if (name.isEmpty()) {
            return;
        }
        try {
            Field field = instance.getClass().getDeclaredField(name);
            tryAddTab(field, instance);
        } catch (Throwable e) {
            plugin.debug(e);
            plugin.consoleKey("extractNoSuchTab", instance.getClass().getName(), name);
        }
    }

    private void tryAddTab(@NotNull Field field, @NotNull Object instance) {
        Tab tab = field.getAnnotation(Tab.class);
        if (tab == null || Modifier.isStatic(field.getModifiers())) {
            return;
        }

        TabExecutor<ICommandSender> tabcutor = null;
        try {
            tabcutor = (TabExecutor) field.get(instance);
        } catch (Throwable e) {
            plugin.debug(e);
        }
        if (tabcutor == null) {
            return;
        }

        Type[] params = Reflects.getActualTypes(TabExecutor.class, tabcutor.getClass());
        if (params == null || params.length != 1 || !Reflects.isAssignableFrom(ICommandSender.class, params[0])) {
            return;
        }

        if (".".equals(tab.path())) {
            this.tabcutor = tabcutor;
            return;
        }

        Paths paths = new Paths(tab.path().isEmpty() ? field.getName().toLowerCase() : tab.path().toLowerCase());
        CommandCore command = getSub(paths);
        if (command != null) {
            command.tabcutor = tabcutor;
        }
    }

    public void sendUsage(ICommandSender sender) {
        sender.sendMessageKey("cmdUsage", getUsage());
    }

    public CommandCore getSub(String name) {
        return subs.get(name);
    }

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

    public CommandCore getParent() {
        return parent;
    }

    public void setTabs(List<String> tabs) {
        this.tabs.clear();
        this.tabs.addAll(tabs);
    }

    public List<String> getTabs() {
        return new ArrayList<>(tabs);
    }

    public void execute(@NotNull ICommandSender sender, @NotNull Args args) {
        CommandCore sub = subs.get(args.first());
        if (sub != null) {
            sub.execute(sender, args.next());
            return;
        }
        if (executor != null) {
            if (testPermission(sender)) {
                if (!ingame || sender instanceof IPlayer) {
                    executor.execute(this, sender, args);
                } else {
                    sender.sendMessageKey("onlyPlayer");
                }
            } else {
                sender.sendMessageKey("noCommandPerm", perm);
            }
        } else {
            sendUsage(sender);
        }
    }

    public List<String> tabComplete(ICommandSender sender, @NotNull Args args) {
        if (tabcutor != null && (!ingame || sender instanceof IPlayer)) {
            return tabcutor.complete(this, sender, args);
        }
        return tabComplete0(sender, args);
    }

    public List<String> tabComplete0(ICommandSender sender, @NotNull Args args) {
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

    public boolean testPermission(@NotNull ICommandSender sender) {
        return perm == null || perm.isEmpty() || sender.hasPermission(perm);
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
