package org.soraworld.violet.api;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.soraworld.rikka.command.ICommandSender;
import org.soraworld.violet.command.InvalidSender;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import rikka.bukkit.BukkitRikka;
import rikka.bukkit.entity.BukkitPlayer;
import rikka.sponge.SpongeRikka;
import rikka.sponge.entity.SpongePlayer;

import java.util.HashMap;

public final class VioletAPI {

    private static ServerType serverType;

    public static final ICommandSender invalid = new InvalidSender();
    private static final HashMap<Object, ICommandSender> senders = new HashMap<>();

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

    public static ICommandSender getCommandSource(org.bukkit.command.CommandSender sender) {
        if (serverType == ServerType.BUKKIT) return BukkitRikka.getCommandSource(sender);
        return invalid;
    }

    public static ICommandSender getCommandSource(org.spongepowered.api.command.CommandSource sender) {
        if (serverType == ServerType.SPONGE) return SpongeRikka.getCommandSource(sender);
        return invalid;
    }

    public static ICommandSender getSender(Object source) {
        if (source == null) return invalid;
        ICommandSender sender = senders.get(source);
        if (sender != null) return sender;
        if (serverType == ServerType.SPONGE) {
            if (source instanceof Player)
                sender = new SpongePlayer<>((org.spongepowered.api.entity.living.player.Player) source);
            else if (source instanceof ICommandSender) sender = new InvalidSender();
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
