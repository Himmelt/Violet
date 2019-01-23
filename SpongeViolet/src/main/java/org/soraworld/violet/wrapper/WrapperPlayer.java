package org.soraworld.violet.wrapper;

import org.soraworld.violet.api.IPlayer;
import org.spongepowered.api.entity.living.player.Player;

public class WrapperPlayer extends WrapperSender<Player> implements IPlayer<Player> {
    public WrapperPlayer(Player sender) {
        super(sender);
    }

    public Player getSender() {
        return super.getSender();
    }
}
