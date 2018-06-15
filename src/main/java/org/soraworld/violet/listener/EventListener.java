package org.soraworld.violet.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import rikka.api.entity.living.IPlayer;

import static rikka.RikkaAPI.getPlayer;

public class EventListener implements BukkitListener {

    @Listener
    public void on(InteractBlockEvent event) {
        Object source = event.getSource();
        if (source instanceof Player) {
            on(getPlayer((Player) source));
        }
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        on(getPlayer(event.getPlayer()));
    }

    private void on(IPlayer player) {
        if (player != null) player.sendMsg("Hello " + player.getName());
    }

}
