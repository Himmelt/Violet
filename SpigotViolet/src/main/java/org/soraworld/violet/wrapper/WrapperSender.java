package org.soraworld.violet.wrapper;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.api.ISender;

public class WrapperSender<T extends CommandSender> implements ISender<T> {
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

    public static ISender build(@NotNull CommandSender sender) {
        if (sender instanceof Player) return new WrapperPlayer((Player) sender);
        else return new WrapperSender<>(sender);
    }
}
