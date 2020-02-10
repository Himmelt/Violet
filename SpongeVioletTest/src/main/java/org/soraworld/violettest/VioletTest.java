package org.soraworld.violettest;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.Violet;
import org.soraworld.violet.plugin.SpongePlugin;
import org.soraworld.violet.text.ChatColor;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Path;

/**
 * @author Himmelt
 */
@Plugin(
        id = "violettest",
        name = "VioletTest",
        version = Violet.PLUGIN_VERSION,
        authors = {"Himmelt"},
        url = "https://github.com/Himmelt/Violet",
        description = "Violet Sponge Test Plugin."
)
public final class VioletTest extends SpongePlugin {

    public VioletTest(Path path, PluginContainer container) {
        super(path, container);
    }

    @Override
    public @NotNull ChatColor chatColor() {
        return ChatColor.AQUA;
    }

    @Override
    public String violetVersion() {
        return "[2.5.0,9.9.9]";
    }
}
