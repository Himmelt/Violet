package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.command.CommandCore;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * @author Himmelt
 */
public interface IPlugin extends IMessenger, IScheduler, I18n {

    String name();

    default String id() {
        return name().toLowerCase().replace(' ', '_');
    }

    String bStatsId();

    default String assetsId() {
        return id();
    }

    String version();

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
