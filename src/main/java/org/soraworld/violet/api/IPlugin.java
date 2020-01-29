package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

public interface IPlugin extends IManager, IMessenger, I18n, IScheduler {

    String getName();

    default String getId() {
        return getName().toLowerCase().replace(' ', '_');
    }

    default String assetsId() {
        return getId();
    }

    String getVersion();

    @NotNull
    Path getRootPath();

    @NotNull
    File getJarFile();

    boolean isEnabled();

    default void onPluginLoad() {
    }

    default void onPluginEnable() {
    }

    default void afterEnable() {
    }

    default void onPluginDisable() {
    }

    default InputStream getAssetStream(String path) {
        return getClass().getResourceAsStream("/assets/" + assetsId() + '/' + path);
    }

    default URL getAssetUrl(String path) {
        return getClass().getResource("/assets/" + assetsId() + '/' + path);
    }

    ClassLoader getClassLoader();

    void addInjectInstance(Object instance);
}
