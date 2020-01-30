package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.text.ChatType;

/**
 * @author Himmelt
 */
public interface ICommandSender {
    boolean hasPermission(@Nullable String permission);

    void sendChat(@NotNull String message);

    void sendChat(@NotNull ChatType type, @NotNull String message);

    void sendMessage(@NotNull String message);

    void sendMessageKey(@NotNull String key, Object... args);

    void sendMessage(@NotNull ChatType type, String message);

    void sendMessageKey(@NotNull ChatType type, @NotNull String key, Object... args);
}
