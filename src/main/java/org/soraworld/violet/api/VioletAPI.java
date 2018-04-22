package org.soraworld.violet.api;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.soraworld.violet.api.command.ICommandSender;
import org.soraworld.violet.command.BukkitSender;
import org.soraworld.violet.command.InvalidSender;
import org.soraworld.violet.command.SpongeSender;
import org.soraworld.violet.entity.BukkitPlayer;
import org.soraworld.violet.entity.SpongePlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;

public final class VioletAPI {

    private static ServerType serverType;

    private static ICommandSender invalid = new InvalidSender();
    private static final HashMap<Object, ICommandSender> senders = new HashMap<>();

    static {
        try {
            Sponge.class.getName();
            serverType = ServerType.SPONGE;
            System.out.println("Running Sponge Server.");
        } catch (Throwable e) {
            try {
                Bukkit.class.getName();
                serverType = ServerType.BUKKIT;
                System.out.println("Running Bukkit Server.");
            } catch (Throwable t) {
                serverType = ServerType.UNKNOWN;
                System.out.println("Running Unknown Server.");
            }
        }
    }

    public static ICommandSender getSender(Object source) {
        if (source == null) return invalid;
        ICommandSender sender = senders.get(source);
        if (sender != null) return sender;
        if (serverType == ServerType.SPONGE) {
            if (source instanceof Player) sender = new SpongePlayer<>((Player) source);
            else if (source instanceof CommandSource) sender = new SpongeSender<>((CommandSource) source);
            senders.put(source, sender);
            return sender;
        }
        if (serverType == ServerType.BUKKIT) {
            if (source instanceof org.bukkit.entity.Player)
                sender = new BukkitPlayer<>((org.bukkit.entity.Player) source);
            else if (source instanceof CommandSender) sender = new BukkitSender<>((CommandSender) source);
            senders.put(source, sender);
            return sender;
        }
        return invalid;
    }

    public static ServerType getServerType() {
        return serverType;
    }

}
