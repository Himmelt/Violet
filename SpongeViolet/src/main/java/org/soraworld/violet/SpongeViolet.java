package org.soraworld.violet;

import org.soraworld.violet.command.SpongeBaseSubs;
import org.soraworld.violet.command.SpongeCommand;
import org.soraworld.violet.manager.SpongeManager;
import org.soraworld.violet.plugin.SpongePlugin;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;
import java.util.List;

/**
 * SpongeViolet 插件.
 */
@Plugin(
        id = Violet.PLUGIN_ID,
        name = Violet.PLUGIN_NAME,
        version = Violet.PLUGIN_VERSION,
        authors = {"Himmelt"},
        url = "https://github.com/Himmelt/Violet",
        description = "Violet Plugin Library."
)
public class SpongeViolet extends SpongePlugin {

    public SpongeManager registerManager(Path path) {
        return new SpongeManager.Manager(this, path);
    }

    public List<Object> registerListeners() {
        return null;
    }

    public void registerCommands() {
        SpongeCommand command = new SpongeCommand(getId(), manager.defAdminPerm(), false, manager);
        command.extractSub(SpongeBaseSubs.class);
        command.setUsage("/violet lang|debug|save|reload|rextract");
        register(this, command);
    }

    public String assetsId() {
        return Violet.ASSETS_ID;
    }

    public void afterEnable() {
        if (manager instanceof SpongeManager.Manager) {
            ((SpongeManager.Manager) manager).startBstats();
        }
    }
}
