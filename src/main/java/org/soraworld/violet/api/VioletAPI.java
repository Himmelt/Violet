package org.soraworld.violet.api;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.soraworld.rikka.command.CommandSource;
import org.soraworld.violet.command.InvalidSender;
import org.soraworld.violet.rikka.bukkit.entity.BukkitPlayer;
import org.soraworld.violet.rikka.sponge.entity.SpongePlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;

public final class VioletAPI {

    private static ServerType serverType;

    private static CommandSource invalid = new InvalidSender();
    private static final HashMap<Object, CommandSource> senders = new HashMap<>();

    static {
        if (Bukkit.getServer() != null) {
            serverType = ServerType.BUKKIT;
            System.out.println("Running Bukkit Server.");
        } else {
            try {
                Sponge.getServer();
                serverType = ServerType.SPONGE;
                System.out.println("Running Sponge Server.");
            } catch (Throwable e) {
                serverType = ServerType.UNKNOWN;
                System.out.println("Running Unknown Server.");
            }
        }
    }

    public static CommandSource getSender(Object source) {
        if (source == null) return invalid;
        CommandSource sender = senders.get(source);
        if (sender != null) return sender;
        if (serverType == ServerType.SPONGE) {
            if (source instanceof Player)
                sender = new SpongePlayer<>((org.spongepowered.api.entity.living.player.Player) source);
            else if (source instanceof CommandSource) sender = new InvalidSender();
            senders.put(source, sender);
            return sender;
        }
        if (serverType == ServerType.BUKKIT) {
            if (source instanceof org.bukkit.entity.Player)
                sender = new BukkitPlayer<>((org.bukkit.entity.Player) source);
            else if (source instanceof CommandSender) sender = new InvalidSender();
            senders.put(source, sender);
            return sender;
        }
        return invalid;
    }

    public static ServerType getServerType() {
        return serverType;
    }

}
