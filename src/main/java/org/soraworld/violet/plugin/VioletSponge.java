package org.soraworld.violet.plugin;

import org.soraworld.violet.Violet;
import org.soraworld.violet.constant.Violets;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;

@Plugin(
        id = Violets.PLUGIN_ID,
        name = Violets.PLUGIN_NAME,
        version = Violets.PLUGIN_VERSION,
        authors = {"Himmelt"},
        url = "https://github.com/Himmelt/Violet",
        description = "Violet Plugin Library."
)
public class VioletSponge {

    @ConfigDir(sharedRoot = false)
    private Path path = new File(Violets.PLUGIN_NAME).toPath();
    private VioletPlugin plugin = new Violet();

    @Listener
    public void onEnable(GameInitializationEvent event) {
        plugin.onLoad();
        plugin.onEnable(path);
    }

    @Listener
    public void onDisable(GameStoppingServerEvent event) {
        plugin.onDisable();
    }

}
