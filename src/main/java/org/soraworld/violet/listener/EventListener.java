package org.soraworld.violet.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.soraworld.rikka.command.CommandSource;
import org.soraworld.rikka.entity.living.player.Player;
import org.soraworld.violet.api.VioletAPI;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;

public class EventListener implements BukkitListener {

    @Listener
    public void on(InteractBlockEvent event) {
        CommandSource sender = VioletAPI.getSender(event.getSource());
        if (sender instanceof Player) {
            on((Player) sender);
        }
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        CommandSource sender = VioletAPI.getSender(event.getPlayer());
        if (sender instanceof Player) {
            on((Player) sender);
        }
    }

    private void on(Player player) {
        player.sendMessage("Hello " + player.getName());
    }

}
