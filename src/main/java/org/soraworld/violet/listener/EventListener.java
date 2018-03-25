package org.soraworld.violet.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;
import org.soraworld.violet.config.IIConfig;

public class EventListener implements Listener {

    private final IIConfig config;
    private final Plugin plugin;

    public EventListener(IIConfig config, Plugin plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        config.save();
    }

}
