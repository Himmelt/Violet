package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.manager.IManager;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

/**
 * The interface Plugin.
 *
 * @param <M> the type parameter
 * @author Himmelt
 */
public interface IPlugin<M extends IManager> {

    /**
     * Gets name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets id.
     *
     * @return the id
     */
    default String getId() {
        return getName().toLowerCase().replace(' ', '_');
    }

    /**
     * Assets id string.
     *
     * @return the string
     */
    default String assetsId() {
        return getId();
    }

    /**
     * Gets version.
     *
     * @return the version
     */
    String getVersion();

    /**
     * Gets root path.
     *
     * @return the root path
     */
    Path getRootPath();

    /**
     * Before load.
     */
    void beforeLoad();

    /**
     * Is enabled boolean.
     *
     * @return the boolean
     */
    boolean isEnabled();

    /**
     * After enable.
     */
    default void afterEnable() {
    }

    /**
     * Before disable.
     */
    default void beforeDisable() {
    }

    /**
     * Gets manager.
     *
     * @return the manager
     */
    M getManager();

    /**
     * Sets manager.
     *
     * @param manager the manager
     */
    void setManager(M manager);

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
     * Register manager m.
     *
     * @param path the path
     * @return the m
     */
    @Nullable
    default M registerManager(@NotNull Path path) {
        return null;
    }

    /**
     * Register inject class.
     *
     * @param clazz the clazz
     */
    void registerInjectClass(@NotNull Class<?> clazz);

    /**
     * Register inject classes.
     */
    default void registerInjectClasses() {
    }

    /**
     * Register commands.
     */
    default void registerCommands() {
    }

    /**
     * Register listeners.
     */
    default void registerListeners() {
    }
}
