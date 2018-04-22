package org.soraworld.violet.api;

import org.bukkit.Bukkit;
import org.soraworld.violet.command.BukkitSender;
import org.soraworld.violet.command.InvalidSender;
import org.soraworld.violet.command.SpongeSender;
import org.spongepowered.api.Sponge;

import java.util.HashMap;

public final class VioletAPI {

    private static ServerType serverType;

    private static VioletSender invalid = new InvalidSender();
    private static final HashMap<Object, VioletSender> senders = new HashMap<>();

    static {
        try {
            Sponge.class.getName();
            serverType = ServerType.SPONGE;
        } catch (Throwable e) {
            try {
                Bukkit.class.getName();
                serverType = ServerType.BUKKIT;
                System.out.println("Running Bukkit Server.");
            } catch (Throwable t) {
                serverType = ServerType.UNKNOWN;
                System.out.println("Running Sponge Server.");
            }
        }
    }

    public static VioletSender getSender(Object source) {
        if (source == null) return invalid;
        VioletSender sender = senders.get(source);
        if (sender != null) return sender;
        if (serverType == ServerType.BUKKIT) {
            sender = new BukkitSender(source);
            senders.put(source, sender);
            return sender;
        }
        if (serverType == ServerType.SPONGE) {
            sender = new SpongeSender(source);
            senders.put(source, sender);
            return sender;
        }
        return invalid;
    }

}
