package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.soraworld.violet.api.VioletSender;

public class BukkitSender implements VioletSender {

    private CommandSender bukkit = null;

    public BukkitSender(Object source) {
        try {
            if (source instanceof CommandSender) bukkit = (CommandSender) source;
        } catch (Throwable ignored) {
        }
    }

    public String getName() {
        return bukkit == null ? "null" : bukkit.getName();
    }

    public boolean hasPermission(String perm) {
        return bukkit != null && bukkit.hasPermission(perm);
    }

    public void sendMessage(String msg) {
        if (bukkit != null) bukkit.sendMessage(msg);
    }

}
