package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.text.ChatColor;
import org.soraworld.violet.text.ChatType;

/**
 * @author Himmelt
 */
public interface IMessenger {

    default @NotNull ChatColor chatColor() {
        return ChatColor.LIGHT_PURPLE;
    }

    void sendChat(@NotNull ICommandSender sender, @NotNull String message);

    void sendChatKey(@NotNull ICommandSender sender, @NotNull String key, Object... args);

    void sendMessage(@NotNull ICommandSender sender, @NotNull String message);

    void sendMessageKey(@NotNull ICommandSender sender, @NotNull String key, Object... args);

    void sendChat(@NotNull IPlayer player, @NotNull ChatType type, @NotNull String message);

    void sendChatKey(@NotNull IPlayer player, @NotNull ChatType type, @NotNull String key, Object... args);

    void sendMessage(@NotNull IPlayer player, @NotNull ChatType type, String message);

    void sendMessageKey(@NotNull IPlayer player, @NotNull ChatType type, @NotNull String key, Object... args);

    void console(@NotNull String message);

    void consoleKey(String key, Object... args);

    void log(@NotNull String text);

    void logKey(@NotNull String key, Object... args);

    void consoleLog(@NotNull String text);

    void consoleLogKey(@NotNull String key, Object... args);

    void broadcast(@NotNull String message);

    void broadcastKey(@NotNull String key, Object... args);

    void debug(@NotNull String message);

    void debug(@NotNull Throwable e);

    void debugKey(@NotNull String key, Object... args);

    void notifyOps(@NotNull String message);

    void notifyOpsKey(@NotNull String key, Object... args);
}
