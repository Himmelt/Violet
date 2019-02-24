package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.manager.IManager;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

public interface IPlugin<M extends IManager> {

    String getName();

    default String getId() {
        return getName().toLowerCase().replace(' ', '_');
    }

    default String assetsId() {
        return getId();
    }

    String getVersion();

    Path getRootPath();

    boolean isEnabled();

    default void afterEnable() {
    }

    default void beforeDisable() {
    }

    M getManager();

    void setManager(M manager);

    String updateURL();

    default InputStream getAssetStream(String path) {
        return getClass().getResourceAsStream("/assets/" + assetsId() + '/' + path);
    }

    default URL getAssetURL(String path) {
        return getClass().getResource("/assets/" + assetsId() + '/' + path);
    }

    @Nullable
    default M registerManager(@NotNull Path path) {
        return null;
    }

    void registerInjectClass(@NotNull Class<?> clazz);

    default void registerInjectClasses() {
    }

    default void registerCommands() {
    }

    default void registerListeners() {
    }
}
