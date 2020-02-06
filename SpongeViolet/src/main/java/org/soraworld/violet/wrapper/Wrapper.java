package org.soraworld.violet.wrapper;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.api.ICommandSender;
import org.soraworld.violet.api.IPlayer;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.core.PluginCore;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.text.ChatType;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * @author Himmelt
 */
@Inject
public final class Wrapper {

    @Inject
    private static IPlugin plugin;
    @Inject
    private static PluginCore core;

    public static ICommandSender wrapper(@NotNull CommandSource sender) {
        if (sender instanceof Player) {
            return new IPlayer() {

                private final Player player = (Player) sender;

                @Override
                public boolean hasPermission(String permission) {
                    return permission == null || permission.isEmpty() || sender.hasPermission(permission);
                }

                @Override
                public void sendChat(@NotNull String message) {
                    player.sendMessage(Text.of(message));
                }

                @Override
                public void sendChat(@NotNull ChatType type, @NotNull String message) {

                }

                @Override
                public void sendMessage(@NotNull String message) {
                    player.sendMessage(Text.of(core.getChatHead() + message));
                }

                @Override
                public void sendMessageKey(@NotNull String key, Object... args) {
                    player.sendMessage(Text.of(core.getChatHead() + plugin.trans(key, args)));
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
                    sender.sendMessage(Text.of(message));
                }

                @Override
                public void sendChat(@NotNull ChatType type, @NotNull String message) {

                }

                @Override
                public void sendMessage(@NotNull String message) {
                    sender.sendMessage(Text.of(core.getChatHead() + message));
                }

                @Override
                public void sendMessageKey(@NotNull String key, Object... args) {
                    sender.sendMessage(Text.of(core.getChatHead() + plugin.trans(key, args)));
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
