package org.soraworld.violet.wrapper;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.api.ICommandSender;
import org.soraworld.violet.api.IPlayer;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.core.PluginCore;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.text.ChatType;

/**
 * @author Himmelt
 */
@Inject
public class Wrapper {

    @Inject
    private static IPlugin plugin;
    @Inject
    private static PluginCore core;

    public static ICommandSender wrapper(@NotNull CommandSender sender) {
        if (sender instanceof Player) {
            return new IPlayer() {

                private final Player player = (Player) sender;

                @Override
                public boolean hasPermission(String permission) {
                    return permission == null || permission.isEmpty() || sender.hasPermission(permission);
                }

                @Override
                public void sendChat(@NotNull String message) {
                    player.sendMessage(message);
                }

                @Override
                public void sendChat(@NotNull ChatType type, @NotNull String message) {

                }

                @Override
                public void sendMessage(@NotNull String message) {
                    player.sendMessage(core.getChatHead() + message);
                }

                @Override
                public void sendMessageKey(@NotNull String key, Object... args) {
                    player.sendMessage(core.getChatHead() + core.trans(key, args));
                }

                @Override
                public void sendMessage(@NotNull ChatType type, String message) {

                }

                @Override
                public void sendMessageKey(@NotNull ChatType type, @NotNull String key, Object... args) {

                }
            };
        } else {
            return new ICommandSender() {
                @Override
                public boolean hasPermission(String permission) {
                    return permission == null || permission.isEmpty() || sender.hasPermission(permission);
                }

                @Override
                public void sendChat(@NotNull String message) {
                    sender.sendMessage(message);
                }

                @Override
                public void sendChat(@NotNull ChatType type, @NotNull String message) {

                }

                @Override
                public void sendMessage(@NotNull String message) {
                    sender.sendMessage(core.getChatHead() + message);
                }

                @Override
                public void sendMessageKey(@NotNull String key, Object... args) {
                    sender.sendMessage(core.getChatHead() + core.trans(key, args));
                }

                @Override
                public void sendMessage(@NotNull ChatType type, String message) {

                }

                @Override
                public void sendMessageKey(@NotNull ChatType type, @NotNull String key, Object... args) {

                }
            };
        }
    }
}
