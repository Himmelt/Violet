package org.soraworld.violettest;

import org.soraworld.violet.text.ChatColor;
import org.soraworld.violet.text.ChatType;
import org.soraworld.violet.util.Helper;
import org.soraworld.violet.wrapper.Wrapper;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * @author Himmelt
 */
@org.soraworld.violet.inject.Listener
public class PlayerEventListener {
    @Listener
    public void onPlayerInteract(InteractItemEvent.Secondary event, @First Player player) {
        Location<World> look = Helper.getLookAt(player, 30);
        Wrapper.wrapper(player).sendMessage(ChatType.ACTION_BAR, ChatColor.GREEN + "look:" + (look == null ? "null" : look.getBlockType()));
    }
}
