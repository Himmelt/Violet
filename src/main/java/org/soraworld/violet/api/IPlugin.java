package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.Violet;
import org.soraworld.violet.command.CommandCore;
import org.soraworld.violet.core.PluginCore;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

/**
 * The interface Plugin.
 *
 * @author Himmelt
 */
public interface IPlugin extends IMessenger, IScheduler, I18n {

    /**
     * Name string.
     *
     * @return the string
     */
    String name();

    /**
     * Id string.
     *
     * @return the string
     */
    default String id() {
        return name().toLowerCase().replace(' ', '_');
    }

    /**
     * B stats id string.
     *
     * @return the string
     */
    String bStatsId();

    /**
     * Assets id string.
     *
     * @return the string
     */
    default String assetsId() {
        return id();
    }

    /**
     * Version string.
     *
     * @return the string
     */
    String version();

    /**
     * Gets core.
     *
     * @return the core
     */
    @NotNull PluginCore getCore();

    /**
     * Gets root path.
     *
     * @return the root path
     */
    @NotNull Path getRootPath();

    /**
     * Gets jar file.
     *
     * @return the jar file
     */
    @NotNull File getJarFile();

    /**
     * Is enabled boolean.
     *
     * @return the boolean
     */
    boolean isEnabled();

    /**
     * On plugin load.
     */
    default void onPluginLoad() {
    }

    /**
     * On plugin enable.
     */
    default void onPluginEnable() {
    }

    /**
     * After enable.
     */
    default void afterEnable() {
    }

    /**
     * On plugin disable.
     */
    default void onPluginDisable() {
    }

    /**
     * Gets asset stream.
     *
     * @param path the path
     * @return the asset stream
     */
    default InputStream getAssetStream(String path) {
        return getClass().getResourceAsStream("/assets/" + assetsId() + '/' + path);
    }

    /**
     * Gets asset url.
     *
     * @param path the path
     * @return the asset url
     */
    default URL getAssetUrl(String path) {
        return getClass().getResource("/assets/" + assetsId() + '/' + path);
    }

    /**
     * Add enable action.
     *
     * @param action the action
     */
    default void addEnableAction(@NotNull Runnable action) {
        getCore().addEnableAction(action);
    }

    /**
     * Add disable action.
     *
     * @param action the action
     */
    default void addDisableAction(@NotNull Runnable action) {
        getCore().addDisableAction(action);
    }

    /**
     * Register listener.
     *
     * @param listener the listener
     */
    void registerListener(@NotNull Object listener);

    /**
     * Register command boolean.
     *
     * @param core the core
     * @return the boolean
     */
    boolean registerCommand(@NotNull CommandCore core);

    /**
     * Register command boolean.
     *
     * @param command the command
     * @param aliases the aliases
     * @return the boolean
     */
    boolean registerCommand(@NotNull Object command, String... aliases);

    /**
     * Register command boolean.
     *
     * @param command the command
     * @param aliases the aliases
     * @return the boolean
     */
    boolean registerCommand(@NotNull Object command, @NotNull List<String> aliases);

    /**
     * 运行所需 violet 的版本.
     * 例如:
     * - 2.5.0
     * - [2.5.0,2.5.5]
     * - (2.5.0,2.6.0]
     *
     * @return violet 版本
     */
    default String violetVersion() {
        return Violet.PLUGIN_VERSION;
    }
}
