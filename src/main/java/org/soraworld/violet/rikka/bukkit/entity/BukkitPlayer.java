package org.soraworld.violet.rikka.bukkit.entity;

import org.soraworld.rikka.entity.living.player.Player;

public class BukkitPlayer<T extends org.bukkit.entity.Player> extends BukkitEntity<T> implements Player {

    public BukkitPlayer(T source) {
        super(source);
    }

}
