package org.soraworld.violet.wrapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.api.ISender;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

public class WrapperSender<T extends CommandSource> implements ISender<T> {
    protected final T sender;

    public WrapperSender(T sender) {
        this.sender = sender;
    }

    public boolean hasPermission(@Nullable String permission) {
        return permission == null || sender.hasPermission(permission);
    }

    public T getSender() {
        return sender;
    }

    public static ISender build(@NotNull CommandSource sender) {
        if (sender instanceof Player) return new WrapperPlayer((Player) sender);
        else return new WrapperSender<>(sender);
    }
}
