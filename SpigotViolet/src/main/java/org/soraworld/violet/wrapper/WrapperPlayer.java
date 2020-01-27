package org.soraworld.violet.wrapper;

import org.bukkit.entity.Player;
import org.soraworld.violet.api.IPlayer;

/**
 * @author Himmelt
 */
public class WrapperPlayer extends WrapperCommandSender<Player> implements IPlayer {
    public WrapperPlayer(Player player) {
        super(player);
    }
}
