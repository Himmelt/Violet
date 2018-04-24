package org.soraworld.violet.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.soraworld.rikka.command.ICommandSender;
import org.soraworld.rikka.entity.living.player.IPlayer;
import org.soraworld.violet.api.VioletAPI;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;

public class EventListener implements BukkitListener {

    @Listener
    public void on(InteractBlockEvent event) {
        ICommandSender sender = VioletAPI.getSender(event.getSource());
        if (sender instanceof IPlayer) {
            on((IPlayer) sender);
        }
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        ICommandSender sender = VioletAPI.getSender(event.getPlayer());
        if (sender instanceof IPlayer) {
            on((IPlayer) sender);
        }
    }

    private void on(IPlayer player) {
        player.sendMessage("Hello " + player.getName());
    }

}
