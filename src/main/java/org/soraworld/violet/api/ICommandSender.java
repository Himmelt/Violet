package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Himmelt
 */
public interface ICommandSender {
    boolean hasPermission(@Nullable String permission);

    void sendMessage(@NotNull String message);
}
