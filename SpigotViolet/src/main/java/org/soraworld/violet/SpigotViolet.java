package org.soraworld.violet;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.soraworld.violet.command.CommandArgs;
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

    @Nonnull
    public SpigotCommand registerCommand() {
        return new SpigotCommand.CommandViolet(null, false, manager, getId());
    }

    @Nullable
    public List<Listener> registerListeners() {
        return null;
    }

    @Nonnull
    public String assetsId() {
        return Violet.ASSETS_ID;
    }

    public void afterEnable() {
        command.addSub(new SpigotCommand(manager.defAdminPerm(), false, manager, "plugins") {
            public void execute(CommandSender sender, CommandArgs args) {
                if (manager instanceof SpigotManager.Manager) {
                    ((SpigotManager.Manager) manager).listPlugins(sender);
                }
            }
        });
        if (manager instanceof SpigotManager.Manager) {
            ((SpigotManager.Manager) manager).startBstats();
        }
    }
}