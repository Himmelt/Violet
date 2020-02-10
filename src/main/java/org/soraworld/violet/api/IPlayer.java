package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.text.ChatType;

/**
 * @author Himmelt
 */
public interface IPlayer extends ICommandSender {
    void sendMessage(@NotNull ChatType type, @NotNull String message);
}
