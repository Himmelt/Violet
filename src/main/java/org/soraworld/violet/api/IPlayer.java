package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.text.ChatType;

/**
 * @author Himmelt
 */
public interface IPlayer extends ICommandSender {

    void sendChat(@NotNull ChatType type, @NotNull String message);

    void sendMessage(@NotNull ChatType type, String message);

    void sendMessageKey(@NotNull ChatType type, @NotNull String key, Object... args);
}
