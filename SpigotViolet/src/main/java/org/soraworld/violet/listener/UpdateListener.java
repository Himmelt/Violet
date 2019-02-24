package org.soraworld.violet.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.soraworld.violet.inject.EventListener;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.manager.VManager;

@EventListener
public class UpdateListener implements Listener {
    @Inject
    private VManager manager;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            manager.checkUpdate(event.getPlayer());
        }
    }
}
