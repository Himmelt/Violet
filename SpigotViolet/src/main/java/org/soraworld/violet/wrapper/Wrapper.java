package org.soraworld.violet.wrapper;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.api.ICommandSender;
import org.soraworld.violet.api.IPlayer;
import org.soraworld.violet.core.PluginCore;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.nms.Helper;
import org.soraworld.violet.text.ChatType;

/**
 * @author Himmelt
 */
@Inject
public final class Wrapper {

    @Inject
    private static PluginCore core;

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
        public void sendChat(@NotNull String message) {
            source.sendMessage(message);
        }

        @Override
        public void sendMessage(@NotNull String message) {
            source.sendMessage(core.getChatHead() + message);
        }

        @Override
        public void sendMessageKey(@NotNull String key, Object... args) {
            source.sendMessage(core.getChatHead() + core.trans(key, args));
        }
    }

    private static class WrapperPlayer extends WrapperCommandSender<Player> implements IPlayer {

        public WrapperPlayer(@NotNull Player player) {
            super(player);
        }

        @Override
        public void sendChat(@NotNull ChatType type, @NotNull String message) {
            Helper.sendChatPacket(source, type, message);
        }

        @Override
        public void sendChatKey(@NotNull ChatType type, @NotNull String key, Object... args) {
            Helper.sendChatPacket(source, type, core.trans(key, args));
        }

        @Override
        public void sendMessage(@NotNull ChatType type, String message) {
            Helper.sendChatPacket(source, type, core.getChatHead() + message);
        }

        @Override
        public void sendMessageKey(@NotNull ChatType type, @NotNull String key, Object... args) {
            Helper.sendChatPacket(source, type, core.getChatHead() + core.trans(key, args));
        }
    }
}
