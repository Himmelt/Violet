package org.soraworld.violet.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.soraworld.violet.Violet;

public class VioletBukkit extends JavaPlugin {

    private VioletPlugin plugin = new Violet();

    public void onLoad() {
        plugin.onLoad();
    }

    public void onEnable() {
        plugin.onEnable(getDataFolder().toPath());
    }

    public void onDisable() {
        plugin.onDisable();
    }

}
