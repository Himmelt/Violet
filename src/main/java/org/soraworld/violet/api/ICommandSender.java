package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Himmelt
 */
public interface ICommandSender {
    String getName();

    boolean hasPermission(@Nullable String permission);

    void sendMessage(@NotNull String message);

    Object getHandle();

    <C> C getHandle(Class<C> clazz);
}
