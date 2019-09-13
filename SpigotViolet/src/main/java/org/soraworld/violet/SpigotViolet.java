package org.soraworld.violet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.soraworld.violet.manager.FManager;
import org.soraworld.violet.plugin.SpigotPlugin;

import java.util.UUID;

/**
 * @author Himmelt
 */
public class SpigotViolet extends SpigotPlugin<FManager> {

    private static SpigotViolet instance;

    {
        instance = this;
    }

    @Override
    public FManager getManager() {
        return manager;
    }

    @Override
    public void afterEnable() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            manager.asyncLoadData(player.getUniqueId());
        }
    }

    @Override
    public void beforeDisable() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            manager.saveData(player.getUniqueId(), false);
        }
    }

    public static UUID getUuid() {
        return instance.manager.getUuid();
    }
}
