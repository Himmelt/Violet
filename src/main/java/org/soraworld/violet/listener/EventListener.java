package org.soraworld.violet.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.soraworld.violet.api.VioletAPI;
import org.soraworld.violet.api.command.ICommandSender;
import org.soraworld.violet.api.entity.IPlayer;
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
