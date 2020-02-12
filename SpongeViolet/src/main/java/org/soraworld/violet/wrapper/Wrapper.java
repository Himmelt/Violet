package org.soraworld.violet.wrapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.api.ICommandSender;
import org.soraworld.violet.api.IPlayer;
import org.soraworld.violet.api.IUser;
import org.soraworld.violet.gamemode.GameMode;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.text.ChatType;
import org.soraworld.violet.world.BlockPos;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

/**
 * @author Himmelt
 */
@Inject
public final class Wrapper {

    public static @NotNull ICommandSender wrapper(@NotNull CommandSource source) {
        if (source instanceof Player) {
            return new WrapperPlayer((Player) source);
        } else {
            return new WrapperCommandSender<>(source);
        }
    }

    public static @NotNull IPlayer wrapper(@NotNull Player source) {
        return new WrapperPlayer(source);
    }

    public static @NotNull IUser wrapper(@NotNull User source) {
        if (source instanceof Player) {
            return new WrapperPlayer((Player) source);
        } else {
            return new WrapperUser(source);
        }
    }

    public static @Nullable IPlayer wrapper(@NotNull String name) {
        Player source = Sponge.getServer().getPlayer(name).orElse(null);
        return source == null ? null : wrapper(source);
    }

    public static @Nullable IPlayer wrapper(@NotNull UUID uuid) {
        Player source = Sponge.getServer().getPlayer(uuid).orElse(null);
        return source == null ? null : wrapper(source);
    }

    public static @NotNull BlockPos wrapper(@NotNull Location<World> location) {
        return new BlockPos(location.getExtent().getUniqueId(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    private static class WrapperCommandSender<T extends CommandSource> implements ICommandSender {

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
            source.sendMessage(Text.of(message));
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
            source.kick();
        }

        @Override
        public void kick(String reason) {
            source.kick(Text.of(reason));
        }

        @Override
        public GameMode gameMode() {
            Object mode = source.gameMode().get();
            if (mode == GameModes.CREATIVE) {
                return GameMode.CREATIVE;
            } else if (mode == GameModes.ADVENTURE) {
                return GameMode.ADVENTURE;
            } else if (mode == GameModes.SPECTATOR) {
                return GameMode.SPECTATOR;
            } else {
                return GameMode.CREATIVE;
            }
        }

        @Override
        public UUID uuid() {
            return source.getUniqueId();
        }

        @Override
        public UUID worldId() {
            return source.getWorld().getUniqueId();
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

    private static class WrapperUser implements IUser {

        private final User source;

        private WrapperUser(User source) {
            this.source = source;
        }

        @Override
        public UUID uuid() {
            return source.getUniqueId();
        }

        @Override
        public String getName() {
            return source.getName();
        }

        @Override
        public User getHandle() {
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
}
