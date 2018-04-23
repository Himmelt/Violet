package org.soraworld.violet.rikka.sponge.entity;

import org.soraworld.rikka.entity.living.player.Player;

public class SpongePlayer<T extends org.spongepowered.api.entity.living.player.Player> extends SpongeEntity<T> implements Player {

    public SpongePlayer(T source) {
        super(source);
    }

}
