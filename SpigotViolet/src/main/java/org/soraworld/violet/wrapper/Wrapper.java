package org.soraworld.violet.wrapper;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.api.ICommandSender;
import org.soraworld.violet.api.IPlayer;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.nms.Helper;
import org.soraworld.violet.text.ChatType;

/**
 * @author Himmelt
 */
@Inject
public final class Wrapper {

    public static ICommandSender wrapper(@NotNull CommandSender source) {
        if (source instanceof Player) {
            return new WrapperPlayer((Player) source);
        } else {
            return new WrapperCommandSender<>(source);
        }
    }

    public static IPlayer wrapper(@NotNull Player source) {
        return new WrapperPlayer(source);
    }

    private static class WrapperCommandSender<T extends CommandSender> implements ICommandSender {

        final T source;

        public WrapperCommandSender(@NotNull T source) {
            this.source = source;
        }

        @Override
        public boolean hasPermission(String permission) {
            return permission == null || permission.isEmpty() || source.hasPermission(permission);
        }

        @Override
        public void sendMessage(@NotNull String message) {
            source.sendMessage(message);
        }
    }

    private static class WrapperPlayer extends WrapperCommandSender<Player> implements IPlayer {

        public WrapperPlayer(@NotNull Player player) {
            super(player);
        }

        @Override
        public void sendMessage(@NotNull ChatType type, @NotNull String message) {
            Helper.sendChatPacket(source, type, message);
        }
    }
}
