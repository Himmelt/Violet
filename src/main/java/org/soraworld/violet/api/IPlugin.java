package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.core.ManagerCore;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

/**
 * The interface Plugin.
 *
 * @author Himmelt
 */
public interface IPlugin {

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
    @NotNull
    Path getRootPath();

    /**
     * Get jar path path.
     *
     * @return the path
     */
    @NotNull
    File getJarFile();

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
     * Before disable.
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

    IManager getManager();

    ManagerCore getManagerCore();

    ClassLoader getClassLoader();
}
