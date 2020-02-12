package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.Violet;
import org.soraworld.violet.command.CommandCore;
import org.soraworld.violet.core.PluginCore;
import org.soraworld.violet.text.ChatType;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

/**
 * The interface Plugin.
 *
 * @author Himmelt
 */
public interface IPlugin extends IMessenger, IScheduler {

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
     * 所有的 enable action 必须在 enable 之前添加。
     * 推荐在 {@link IPlugin#onPluginLoad()} 处添加。
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

    default String trans(@NotNull String key, Object... args) {
        return getCore().trans(key, args);
    }

    @Override
    default void consoleKey(String key, Object... args) {
        console(getCore().trans(key, args));
    }

    @Override
    default void log(@NotNull String text) {
        getCore().log(text);
    }

    @Override
    default void logKey(@NotNull String key, Object... args) {
        getCore().log(getCore().trans(key, args));
    }

    @Override
    default void consoleLog(@NotNull String text) {
        console(text);
        log(text);
    }

    @Override
    default void consoleLogKey(@NotNull String key, Object... args) {
        consoleLog(getCore().trans(key, args));
    }

    @Override
    default void broadcastKey(@NotNull String key, Object... args) {
        broadcast(getCore().trans(key, args));
    }

    @Override
    default void debug(@NotNull String message) {
        getCore().debug(message);
    }

    @Override
    default void debug(@NotNull Throwable e) {
        getCore().debug(e);
    }

    @Override
    default void debugKey(@NotNull String key, Object... args) {
        getCore().debugKey(key, args);
    }

    @Override
    default void notifyOpsKey(@NotNull String key, Object... args) {
        notifyOps(getCore().trans(key, args));
    }

    @Override
    default void notifyOpsKey(@NotNull ChatType type, @NotNull String key, Object... args) {
        notifyOps(type, getCore().trans(key, args));
    }

    @Override
    default void sendChat(@NotNull ICommandSender sender, @NotNull String message) {
        sender.sendMessage(message);
    }

    @Override
    default void sendChatKey(@NotNull ICommandSender sender, @NotNull String key, Object... args) {
        sender.sendMessage(getCore().trans(key, args));
    }

    @Override
    default void sendMessage(@NotNull ICommandSender sender, @NotNull String message) {
        sender.sendMessage(getCore().getChatHead() + message);
    }

    @Override
    default void sendMessageKey(@NotNull ICommandSender sender, @NotNull String key, Object... args) {
        sender.sendMessage(getCore().getChatHead() + getCore().trans(key, args));
    }

    @Override
    default void sendChat(@NotNull IPlayer player, @NotNull ChatType type, @NotNull String message) {
        player.sendMessage(type, message);
    }

    @Override
    default void sendChatKey(@NotNull IPlayer player, @NotNull ChatType type, @NotNull String key, Object... args) {
        player.sendMessage(type, getCore().trans(key, args));
    }

    @Override
    default void sendMessage(@NotNull IPlayer player, @NotNull ChatType type, String message) {
        player.sendMessage(type, getCore().getChatHead() + message);
    }

    @Override
    default void sendMessageKey(@NotNull IPlayer player, @NotNull ChatType type, @NotNull String key, Object... args) {
        player.sendMessage(type, getCore().getChatHead() + getCore().trans(key, args));
    }
}
