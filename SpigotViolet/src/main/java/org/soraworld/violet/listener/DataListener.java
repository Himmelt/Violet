package org.soraworld.violet.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.soraworld.violet.data.DataAPI;
import org.soraworld.violet.manager.FBManager;

import java.util.UUID;

public class DataListener implements Listener {

    private final FBManager manager;

    public DataListener(FBManager manager) {
        this.manager = manager;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        manager.asyncLoadData(uuid);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        DataAPI.clearPlayerTemp(uuid);
        manager.asyncSaveData(uuid, true);
    }
}
