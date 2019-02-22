package org.soraworld.violet.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.util.ListUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ICommand extends Command {

    public final String name;
    public final String permission;
    public final ICommand parent;
    public final IManager manager;

    protected String usage;
    protected boolean onlyPlayer;
    protected SubExecutor<ICommand, CommandSender> subExecutor;
    protected TabExecutor<ICommand, CommandSender> tabExecutor;
    protected List<String> tabs = new ArrayList<>();
    protected final Map<String, ICommand> subs = new LinkedHashMap<>();

    public ICommand(@NotNull String name, @Nullable String permission, @Nullable ICommand parent, @NotNull IManager manager) {
        super(name);
        this.name = name;
        this.permission = permission;
        this.parent = parent;
        this.manager = manager;
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

    public void sendUsage(CommandSender sender) {
        if (usage != null && !usage.isEmpty()) {
            manager.sendKey(sender, "cmdUsage", usage);
        }
    }

    /* ---------------------------------------- modify start -------------------------------------------- */

    public void execute(CommandSender sender, Args args) {
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

    public List<String> tabComplete(CommandSender sender, Args args) {
        if (tabExecutor != null) return tabExecutor.complete(this, sender, args);
        String first = args.first();
        if (args.size() == 1) {
            return ListUtils.getMatchList(first, tabs != null && !tabs.isEmpty() ? tabs : subs.keySet());
        }
        if (subs.containsKey(first)) {
            args.next();
            return subs.get(first).tabComplete(sender, args);
        }
        return new ArrayList<>();
    }

    /* ---------------------------------------- origin start -------------------------------------------- */

    public boolean execute(CommandSender sender, String label, String[] args) {
        if (testPermission(sender)) {
            if (!onlyPlayer || sender instanceof Player) {
                execute(sender, new Args(args));
            } else manager.sendKey(sender, "onlyPlayer");
        } else manager.sendKey(sender, "noCommandPerm", permission);
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return tabComplete(sender, new Args(args));
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) {
        return tabComplete(sender, new Args(args));
    }

    public boolean testPermission(CommandSender sender) {
        return permission == null || sender.hasPermission(permission);
    }

    public boolean testPermissionSilent(CommandSender sender) {
        return permission == null || sender.hasPermission(permission);
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof ICommand) return name.equals(((ICommand) obj).name);
        return false;
    }
}
