package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Himmelt
 */
public interface ICommandSender {
    boolean hasPermission(@Nullable String permission);

    void sendChat(@NotNull String message);

    void sendMessage(@NotNull String message);

    void sendMessageKey(@NotNull String key, Object... args);
}
