package org.soraworld.violet.api;

import org.bukkit.event.Listener;
import org.soraworld.violet.command.ICommand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface IPlugin {

    @Nonnull
    String getId();

    default InputStream getAsset(String path) {
        return getClass().getResourceAsStream("/assets/" + getId() + '/' + path);
    }

    @Nonnull
    IManager registerManager(Path path);

    @Nonnull
    List<Listener> registerEvents();

    @Nullable
    ICommand registerCommand();

    void afterEnable();

    void beforeDisable();
}
