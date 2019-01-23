package org.soraworld.violet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.manager.FBManager;
import org.soraworld.violet.plugin.SpigotPlugin;

import java.util.UUID;

/**
 * SpigotViolet 插件.
 */
@Inject
public class SpigotViolet extends SpigotPlugin<FBManager> {

    @Inject
    private static SpigotViolet instance;

    public FBManager getManager() {
        return manager;
    }

    /**
     * 获取 Violet 插件运行 uuid
     *
     * @return the uuid
     */
    public UUID getUUID() {
        return manager.getUUID();
    }

    /**
     * 获取 紫罗兰(id:violet) 插件.
     *
     * @return 紫罗兰插件本体
     */
    public static SpigotViolet getViolet() {
        return instance;
    }

    public void afterEnable() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            manager.asyncLoadData(player.getUniqueId());
        }
    }

    public void beforeDisable() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            manager.saveData(player.getUniqueId(), false);
        }
    }
}
