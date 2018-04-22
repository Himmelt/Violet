package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.soraworld.violet.api.command.ICommandSender;

public class BukkitSender<T extends CommandSender> implements ICommandSender {

    protected final T bukkit;

    public BukkitSender(T source) {
        bukkit = source;
    }

    public String getName() {
        return bukkit.getName();
    }

    public boolean hasPermission(String perm) {
        return bukkit.hasPermission(perm);
    }

    public void sendMessage(String msg) {
        bukkit.sendMessage(msg);
    }

}
