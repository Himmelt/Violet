package org.soraworld.violet;

import org.soraworld.violet.command.CommandArgs;
import org.soraworld.violet.command.SpongeCommand;
import org.soraworld.violet.manager.SpongeManager;
import org.soraworld.violet.plugin.SpongePlugin;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;

/**
 * SpongeViolet 插件.
 */
@Plugin(
        id = Violet.SPONGE_ID,
        name = Violet.SPONGE_NAME,
        version = Violet.PLUGIN_VERSION,
        authors = {"Himmelt"},
        url = "https://github.com/Himmelt/Violet",
        description = "Violet Plugin Library."
)
public class SpongeViolet extends SpongePlugin {

    @Nonnull
    public String getId() {
        return Violet.PLUGIN_ID;
    }

    @Nonnull
    public String getVersion() {
        return Violet.PLUGIN_VERSION;
    }

    @Nonnull
    public SpongeManager registerManager(Path path) {
        return new SpongeManager.Manager(this, path);
    }

    @Nonnull
    public SpongeCommand registerCommand() {
        return new SpongeCommand.CommandViolet(null, false, manager, getId());
    }

    @Nullable
    public List<Object> registerListeners() {
        return null;
    }

    public void afterEnable() {
        command.addSub(new SpongeCommand(manager.defAdminPerm(), false, manager, "plugins") {
            public void execute(CommandSource sender, CommandArgs args) {
                if (manager instanceof SpongeManager.Manager) {
                    ((SpongeManager.Manager) manager).listPlugins(sender);
                }
            }
        });
        super.afterEnable();
    }
}
