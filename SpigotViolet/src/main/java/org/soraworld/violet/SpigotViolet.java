package org.soraworld.violet;

import org.bukkit.event.Listener;
import org.soraworld.violet.command.SpigotCommand;
import org.soraworld.violet.manager.SpigotManager;
import org.soraworld.violet.plugin.SpigotPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;

public class SpigotViolet extends SpigotPlugin {

    @Nonnull
    public String getId() {
        return Violet.PLUGIN_ID;
    }

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
}
