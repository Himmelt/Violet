package org.soraworld.violet;

import org.bukkit.event.Listener;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.command.ICommand;
import org.soraworld.violet.command.VioletCommand;
import org.soraworld.violet.manager.Manager;
import org.soraworld.violet.manager.VioletSettings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Violet extends VioletPlugin {

    @Nonnull
    public String getId() {
        return Violets.PLUGIN_ID;
    }

    @Nonnull
    public IManager registerManager(Path path) {
        return new Manager(this, path, new VioletSettings());
    }

    @Nonnull
    public List<Listener> registerEvents() {
        return new ArrayList<>();
    }

    @Nullable
    public ICommand registerCommand() {
        return new VioletCommand(null, false, manager, Violets.PLUGIN_ID);
    }

    public void afterEnable() {
    }

    public void beforeDisable() {
    }
}
