package org.soraworld.violet.wrapper;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.api.ICommandSender;
import org.soraworld.violet.api.IPlayer;
import org.soraworld.violet.gamemode.GameMode;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.text.ChatType;
import org.soraworld.violet.util.Helper;

import java.util.UUID;

/**
 * @author Himmelt
 */
@Inject
public final class Wrapper {

    public static @NotNull ICommandSender wrapper(@NotNull CommandSender source) {
        if (source instanceof Player) {
            return new WrapperPlayer((Player) source);
        } else {
            return new WrapperCommandSender<>(source);
        }
    }

    public static @NotNull IPlayer wrapper(@NotNull Player source) {
        return new WrapperPlayer(source);
    }

    public static @Nullable IPlayer wrapper(@NotNull String name) {
        Player source = Bukkit.getPlayer(name);
        return source == null ? null : wrapper(source);
    }

    public static @Nullable IPlayer wrapper(@NotNull UUID uuid) {
        Player source = Bukkit.getPlayer(uuid);
        return source == null ? null : wrapper(source);
    }

    public static @NotNull org.soraworld.violet.world.Location wrapper(@NotNull Block block) {
        return new org.soraworld.violet.world.Location(block.getWorld().getUID(), block.getX(), block.getY(), block.getZ());
    }

    private static class WrapperCommandSender<T extends CommandSender> implements ICommandSender {

        final T source;

        public WrapperCommandSender(@NotNull T source) {
            this.source = source;
        }

        @Override
        public String getName() {
            return source.getName();
        }

        @Override
        public boolean hasPermission(String permission) {
            return permission == null || permission.isEmpty() || source.hasPermission(permission);
        }

        @Override
        public void sendMessage(@NotNull String message) {
            source.sendMessage(message);
        }

        @Override
        public T getHandle() {
            return source;
        }

        @Override
        public <C> C getHandle(Class<C> clazz) {
            if (clazz.isAssignableFrom(source.getClass())) {
                return (C) source;
            }
            return null;
        }
    }

    private static class WrapperPlayer extends WrapperCommandSender<Player> implements IPlayer {

        public WrapperPlayer(@NotNull Player player) {
            super(player);
        }

        @Override
        public void kick() {
            source.kickPlayer("");
        }

        @Override
        public void kick(String reason) {
            source.kickPlayer(reason);
        }

        @Override
        public GameMode gameMode() {
            switch (source.getGameMode()) {
                case CREATIVE:
                    return GameMode.CREATIVE;
                case ADVENTURE:
                    return GameMode.ADVENTURE;
                case SPECTATOR:
                    return GameMode.SPECTATOR;
                default:
                    return GameMode.SURVIVAL;
            }
        }

        @Override
        public UUID uuid() {
            return source.getUniqueId();
        }

        @Override
        public UUID worldId() {
            return source.getWorld().getUID();
        }

        @Override
        public void sendMessage(@NotNull ChatType type, @NotNull String message) {
            Helper.sendChatPacket(source, type, message);
        }
    }
}
