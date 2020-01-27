package org.soraworld.violet.wrapper;

import org.soraworld.violet.api.IPlayer;
import org.spongepowered.api.entity.living.player.Player;

/**
 * @author Himmelt
 */
public class WrapperPlayer extends WrapperCommandSender<Player> implements IPlayer {
    public WrapperPlayer(Player player) {
        super(player);
    }
}
