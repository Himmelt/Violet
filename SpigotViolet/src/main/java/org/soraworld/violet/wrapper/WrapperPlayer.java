package org.soraworld.violet.wrapper;

import org.bukkit.entity.Player;
import org.soraworld.violet.api.IPlayer;

public class WrapperPlayer extends WrapperSender<Player> implements IPlayer<Player> {
    public WrapperPlayer(Player sender) {
        super(sender);
    }

    public Player getSender() {
        return super.getSender();
    }
}
