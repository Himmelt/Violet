package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.gamemode.GameMode;
import org.soraworld.violet.text.ChatType;

import java.util.UUID;

/**
 * @author Himmelt
 */
public interface IPlayer extends ICommandSender {
    void kick();

    void kick(String reason);

    GameMode gameMode();

    UUID worldId();

    void sendMessage(@NotNull ChatType type, @NotNull String message);

//    void sendTitle(String title);
//
//    void resetTitle();
//
//    void clearTitle();
}
