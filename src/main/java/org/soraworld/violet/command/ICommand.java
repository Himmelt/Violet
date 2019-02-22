package org.soraworld.violet.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.hocon.node.Paths;
import org.soraworld.hocon.util.Reflects;
import org.soraworld.violet.api.IManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class ICommand {

    private static Method hasPermission;

    protected ICommand(@NotNull String name, @Nullable String permission, @Nullable ICommand parent, @NotNull IManager manager) {
        this.name = name;
        this.permission = permission;
        this.parent = parent;
        this.manager = manager;
    }

    public final String getName() {
        return name;
    }

    IManager getManager() {
        return manager;
    }

    public final boolean isOnlyPlayer() {
        return onlyPlayer;
    }

    public void addSub(@NotNull ICommand sub) {
        ICommand old = subs.get(sub.getName());
        if (old != null) {
            subs.entrySet().removeIf(entry -> entry.getValue() == old);
            if (old != sub) old.subs.forEach(sub.subs::putIfAbsent);
        }
        subs.put(sub.getName(), sub);
        for (String alias : sub.getAliases()) {
            subs.putIfAbsent(alias, sub);
        }
    }

    public List<String> getAliases() {
        return new ArrayList<>();
    }

    public void tryAddSub(@NotNull Field field, @NotNull Object instance) {
        IManager manager = getManager();
        Sub sub = field.getAnnotation(Sub.class);
        if (sub == null || Modifier.isStatic(field.getModifiers())) return;
        if (!sub.parent().isEmpty() && !sub.parent().equalsIgnoreCase(getName())) return;

        SubExecutor<ICommand, Object> executor = null;
        try {
            executor = (SubExecutor) field.get(instance);
        } catch (Throwable e) {
            if (manager.isDebug()) e.printStackTrace();
        }
        if (executor == null) return;

        Type[] params = Reflects.getActualTypes(SubExecutor.class, executor.getClass());
        if (params == null || params.length != 2
                || !Reflects.isAssignableFrom(params[0], getClass())
                || !Reflects.isAssignableFrom(ICommand.class, params[0])
                || !Reflects.isAssignableFrom(ISender.class, params[1])) return;

        Paths paths = new Paths(sub.path().isEmpty() ? field.getName().toLowerCase() : sub.path().replace(' ', '_').replace(':', '_'));
        String perm = sub.perm().isEmpty() ? null : sub.perm().replace(' ', '_').replace(':', '_');
        if ("admin".equals(perm)) perm = manager.defAdminPerm();
        ICommand command = createSub(paths);
        if (!sub.virtual()) command.subExecutor = executor;
        command.setPermission(perm);
        command.setOnlyPlayer(sub.onlyPlayer() || Reflects.isAssignableFrom(IPlayer.class, params[1]));
        command.setAliases(new ArrayList<>(Arrays.asList(sub.aliases())));
        command.setTabCompletions(sub.tabs());
        command.setUsage(sub.usage());
        command.setDescription(sub.usage());
        command.update();
    }


    public ICommand createSub(Paths paths) {
        Map<String, ICommand> subs = getSubs();
        if (paths.empty()) return this;
        ICommand sub = subs.get(paths.first());
        if (sub == null) sub = build(this, paths.first(), null, false, getManager());
        return sub.createSub(paths.next());
    }

    public ICommand getSub(@NotNull String name) {
        return getSubs().get(name);
    }

    ICommand getParent() {
        return parent;
    }

    public void update() {
        ICommand parent = getParent();
        if (parent != null) {
            parent.addSub(this);
            parent.update();
        }
    }

    public void execute(Object sender, Args args) {
        if (testPermission(sender)) {
            if (!onlyPlayer || sender.getClass().getName().contains("Player")) {
                process(sender, args);
            } else manager.sendKey(sender, "onlyPlayer");
        } else manager.sendKey(sender, "noCommandPerm", permission);
    }

    private void process(Object sender, Args args) {
        if (subExecutor == null) {
            if (args.notEmpty()) {
                ICommand sub = subs.get(args.first());
                if (sub != null) {
                    sub.execute(sender, args.next());
                    return;
                }
            }
            sendUsage(sender);
        } else subExecutor.execute(this, sender, args);
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
            IManager manager = getManager();
            if (manager.isDebug()) e.printStackTrace();
            manager.consoleKey("extractNoSuchSub", instance.getClass().getName(), name);
        }
    }


}
