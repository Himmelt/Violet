package org.soraworld.violet.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Entity selector.
 *
 * @author Himmelt
 */
public class EntitySelector {

    /**
     * Match nearest player player.
     * {@code @p}
     *
     * @param sender the sender
     * @return the player
     */
    public static Player matchNearestPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        Player player = null;
        Location location = null;
        if (sender instanceof BlockCommandSender) {
            location = ((BlockCommandSender) sender).getBlock().getLocation();
        } else if (sender instanceof CommandMinecart) {
            location = ((CommandMinecart) sender).getLocation();
        }
        if (location != null) {
            double minDistance = Double.MAX_VALUE;
            List<Player> players = location.getWorld().getPlayers();
            for (Player p : players) {
                double distance = location.distance(p.getLocation());
                if (distance < minDistance) {
                    player = p;
                    minDistance = distance;
                }
            }
        }
        return player;
    }

    /**
     * Match nearby players list.
     * {@code @p[radius=xxx]}
     *
     * @param sender the sender
     * @param radius the radius
     * @return the list
     */

    public static List<Player> matchNearbyPlayers(CommandSender sender, double radius) {
        Location location = null;
        if (sender instanceof Entity) {
            location = ((Entity) sender).getLocation();
        } else if (sender instanceof BlockCommandSender) {
            location = ((BlockCommandSender) sender).getBlock().getLocation();
        }
        if (location != null) {
            final Location loc = location;
            return location.getWorld().getPlayers().stream().filter(p -> loc.distanceSquared(p.getLocation()) < radius * radius).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * Match around players list.
     * {@code @p[radius=xxx]}
     *
     * @param sender the sender
     * @param radius the radius
     * @return the list
     */
    public static List<Player> matchAroundPlayers(CommandSender sender, double radius) {
        Location location = null;
        if (sender instanceof Entity) {
            location = ((Entity) sender).getLocation();
        } else if (sender instanceof BlockCommandSender) {
            location = ((BlockCommandSender) sender).getBlock().getLocation();
        }
        if (location != null) {
            final Location loc = location;
            return location.getWorld().getPlayers().stream().filter(p -> {
                Location pLoc = p.getLocation();
                return Math.pow(loc.getX() - pLoc.getX(), 2) + Math.pow(loc.getZ() - pLoc.getZ(), 2) < radius * radius;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * Match all players list.
     * {@code @a}
     *
     * @return the list
     */

    public static List<Player> matchAllPlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    /**
     * Match entities list.
     *
     * @param selector the selector
     * @return the list
     */
    public static List<Entity> matchEntities(String selector) {
        return new ArrayList<>();
    }
}
