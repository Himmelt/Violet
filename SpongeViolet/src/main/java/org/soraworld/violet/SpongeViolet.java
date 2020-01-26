package org.soraworld.violet;

import org.soraworld.violet.manager.FManager;
import org.soraworld.violet.plugin.SpongePlugin;
import org.spongepowered.api.plugin.Plugin;

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
}
