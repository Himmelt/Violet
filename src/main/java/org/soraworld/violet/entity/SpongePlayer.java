package org.soraworld.violet.entity;

import org.soraworld.violet.api.entity.IPlayer;
import org.soraworld.violet.command.SpongeSender;
import org.spongepowered.api.entity.living.player.Player;

public class SpongePlayer<T extends Player> extends SpongeSender<T> implements IPlayer {

    public SpongePlayer(T source) {
        super(source);
    }

}
