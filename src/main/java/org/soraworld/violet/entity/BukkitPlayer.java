package org.soraworld.violet.entity;

import org.bukkit.entity.Player;
import org.soraworld.violet.api.entity.IPlayer;
import org.soraworld.violet.command.BukkitSender;

public class BukkitPlayer<T extends Player> extends BukkitSender<T> implements IPlayer {

    public BukkitPlayer(T source) {
        super(source);
    }

}
