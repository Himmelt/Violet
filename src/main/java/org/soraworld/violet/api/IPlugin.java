package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.command.CommandCore;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Himmelt
 */
public interface IPlugin extends IMessenger, IScheduler, I18n {

    String getName();

    default String getId() {
        return getName().toLowerCase().replace(' ', '_');
    }

    default String assetsId() {
        return getId();
    }

    String getVersion();

    @NotNull Path getRootPath();

    @NotNull File getJarFile();

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

    void registerListener(@NotNull Object listener);

    boolean registerCommand(@NotNull CommandCore core);

    boolean registerCommand(@NotNull Object command, String... aliases);

    boolean registerCommand(@NotNull Object command, @NotNull List<String> aliases);
}
