package org.soraworld.violet;

import org.soraworld.violet.manager.FManager;
import org.soraworld.violet.plugin.SpongePlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.Plugin;

import java.util.UUID;

/**
 * @author Himmelt
 */
@Plugin(
        id = Violet.PLUGIN_ID,
        name = Violet.PLUGIN_NAME,
        version = Violet.PLUGIN_VERSION,
        authors = {"Himmelt"},
        url = "https://github.com/Himmelt/Violet",
        description = "Violet Plugin Library."
)
public class SpongeViolet extends SpongePlugin<FManager> {

    private static SpongeViolet instance;

    {
        instance = this;
    }

    @Override
    public void beforeLoad() {
        loadLibraries();
    }

    @Override
    public FManager getManager() {
        return manager;
    }

    @Override
    public void afterEnable() {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            manager.asyncLoadData(player.getUniqueId());
        }
    }

    @Override
    public void beforeDisable() {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            manager.saveData(player.getUniqueId(), false);
        }
    }

    public static UUID getUuid() {
        return instance.manager.getUuid();
    }
}
