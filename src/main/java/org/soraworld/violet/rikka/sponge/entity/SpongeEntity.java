package org.soraworld.violet.rikka.sponge.entity;

import org.soraworld.rikka.entity.Entity;

public class SpongeEntity<T extends org.spongepowered.api.entity.Entity> implements Entity {

    protected final T source;

    public SpongeEntity(T source) {
        this.source = source;
    }

}
