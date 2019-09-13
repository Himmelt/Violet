package org.soraworld.violet.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.soraworld.violet.data.DataAPI;
import org.soraworld.violet.inject.EventListener;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.manager.FManager;

import java.util.UUID;

/**
 * @author Himmelt
 */
@EventListener
public class DataListener implements Listener {

    @Inject
    private FManager manager;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerLoginPre(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        manager.asyncLoadData(uuid);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLoginLast(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            manager.asyncSaveData(uuid, true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        DataAPI.clearTemp(uuid);
        manager.asyncSaveData(uuid, true);
    }
}
