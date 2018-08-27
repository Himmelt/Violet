package org.soraworld.violet;

import org.bukkit.event.Listener;
import org.soraworld.violet.command.SpigotBaseSubs;
import org.soraworld.violet.command.SpigotCommand;
import org.soraworld.violet.manager.SpigotManager;
import org.soraworld.violet.plugin.SpigotPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;

/**
 * SpigotViolet 插件.
 */
public class SpigotViolet extends SpigotPlugin {

    @Nonnull
    public SpigotManager registerManager(Path path) {
        return new SpigotManager.Manager(this, path);
    }

    @Nullable
    public List<Listener> registerListeners() {
        return null;
    }

    public void registerCommands() {
        SpigotCommand command = new SpigotCommand(getId(), manager.defAdminPerm(), false, manager);
        command.extractSub(SpigotBaseSubs.class);
        register(this, command);
    }

    @Nonnull
    public String assetsId() {
        return Violet.ASSETS_ID;
    }

    public void afterEnable() {
        if (manager instanceof SpigotManager.Manager) {
            ((SpigotManager.Manager) manager).startBstats();
        }
    }
}
