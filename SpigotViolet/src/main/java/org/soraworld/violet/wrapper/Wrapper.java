package org.soraworld.violet.wrapper;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.api.ICommandSender;
import org.soraworld.violet.api.IPlayer;
import org.soraworld.violet.core.PluginCore;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.text.ChatType;

import static org.soraworld.violet.Violet.MC_VERSION;
import static org.soraworld.violet.version.McVersion.v1_9_2;

/**
 * @author Himmelt
 */
@Inject
public final class Wrapper {

    @Inject
    private static PluginCore core;

    public static ICommandSender wrapper(@NotNull CommandSender sender) {
        if (sender instanceof Player) {
            return new WrapperPlayer((Player) sender);
        } else {
            return new WrapperCommandSender<>(sender);
        }
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
            if (MC_VERSION.lower(v1_9_2)) {
                source.sendMessage(message);
            } else {
                switch (type) {
                    case ACTION_BAR:
                        source.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
                        break;
                    case SYSTEM:
                        source.spigot().sendMessage(ChatMessageType.SYSTEM, new TextComponent(message));
                        break;
                    default:
                        source.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(message));
                }
            }
        }

        @Override
        public void sendMessage(@NotNull ChatType type, String message) {
            if (MC_VERSION.lower(v1_9_2)) {
                source.sendMessage(message);
            } else {
                switch (type) {
                    case ACTION_BAR:
                        source.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(core.getChatHead() + message));
                        break;
                    case SYSTEM:
                        source.spigot().sendMessage(ChatMessageType.SYSTEM, new TextComponent(core.getChatHead() + message));
                        break;
                    default:
                        source.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(core.getChatHead() + message));
                }
            }
        }

        @Override
        public void sendMessageKey(@NotNull ChatType type, @NotNull String key, Object... args) {
            if (MC_VERSION.lower(v1_9_2)) {
                source.sendMessage(core.getChatHead() + core.trans(key, args));
            } else {
                switch (type) {
                    case ACTION_BAR:
                        source.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(core.getChatHead() + core.trans(key, args)));
                        break;
                    case SYSTEM:
                        source.spigot().sendMessage(ChatMessageType.SYSTEM, new TextComponent(core.getChatHead() + core.trans(key, args)));
                        break;
                    default:
                        source.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(core.getChatHead() + core.trans(key, args)));
                }
            }
        }
    }
}
