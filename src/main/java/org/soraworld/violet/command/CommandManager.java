package org.soraworld.violet.command;

import org.jetbrains.annotations.NotNull;
import org.soraworld.hocon.node.Paths;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.util.ChatColor;

import java.util.TreeMap;

public class CommandManager {

    @Inject
    private static IManager manager;

    private static final TreeMap<String, ICommand> commandsMap = new TreeMap<>();

    public static boolean register(@NotNull ICommand command, @NotNull String... aliases) {
        String name = command.getName();
        ICommand cmd = commandsMap.get(name);
        if (cmd != null) {
            IPlugin plugin = cmd.getManager().getPlugin();
            manager.console(ChatColor.RED + "Command " + name + " has been registered by " + plugin.getName());
            return false;
        }
        commandsMap.put(name, command);
        for (String alias : aliases) {
            cmd = commandsMap.get(alias);
            if (cmd != null) {
                IPlugin plugin = cmd.getManager().getPlugin();
                manager.console(ChatColor.RED + "Command " + name + " has been registered by " + plugin.getName());
                continue;
            }
            commandsMap.put(name, command);
        }
        return true;
    }

    public static ICommand unregister(@NotNull String path) {
        return unregister(new Paths(path));
    }

    public static ICommand unregister(@NotNull Paths paths) {
        if (paths.size() > 1) {
            ICommand command = commandsMap.get(paths.first());
            if (command != null) return command.removeSub(paths.next());
            else return null;
        } else return commandsMap.remove(paths.first());
    }
}
