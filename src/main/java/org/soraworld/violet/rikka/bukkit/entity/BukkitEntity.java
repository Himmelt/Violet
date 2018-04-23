package org.soraworld.violet.rikka.bukkit.entity;

import org.soraworld.rikka.entity.Entity;

public class BukkitEntity<T extends org.bukkit.entity.Entity> implements Entity {

    protected final T source;

    public BukkitEntity(T source) {
        this.source = source;
    }

}
