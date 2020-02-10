package org.soraworld.violet.wrapper;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.api.ICommandSender;
import org.soraworld.violet.api.IPlayer;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.text.ChatType;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;

/**
 * @author Himmelt
 */
@Inject
public final class Wrapper {

    public static ICommandSender wrapper(@NotNull CommandSource source) {
        if (source instanceof Player) {
            return new WrapperPlayer((Player) source);
        } else {
            return new WrapperCommandSender<>(source);
        }
    }

    public static IPlayer wrapper(@NotNull Player source) {
        return new WrapperPlayer(source);
    }

    private static class WrapperCommandSender<T extends CommandSource> implements ICommandSender {

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
            source.sendMessage(Text.of(message));
        }
    }

    private static class WrapperPlayer extends WrapperCommandSender<Player> implements IPlayer {

        public WrapperPlayer(@NotNull Player player) {
            super(player);
        }

        @Override
        public void sendMessage(@NotNull ChatType type, @NotNull String message) {
            switch (type) {
                case ACTION_BAR:
                    source.sendMessage(ChatTypes.ACTION_BAR, Text.of(message));
                    break;
                case SYSTEM:
                    source.sendMessage(ChatTypes.SYSTEM, Text.of(message));
                    break;
                default:
                    source.sendMessage(ChatTypes.CHAT, Text.of(message));
            }
        }
    }
}
