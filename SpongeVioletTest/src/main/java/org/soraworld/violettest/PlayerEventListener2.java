package org.soraworld.violettest;

import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.inject.InjectListener;
import org.soraworld.violet.inject.McVer;
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
@InjectListener
@McVer("[1.7.10,1.10.2]")
public class PlayerEventListener2 {

    @Inject
    private IPlugin plugin;

    static {
        System.out.println("Test @McVer(\"[1.7.10,1.10.2]\")");
    }

    @Listener
    public void onPlayerInteract(InteractItemEvent.Secondary event, @First Player player) {
        Location<World> look = Helper.getLookAt(player, 30);
        Wrapper.wrapper(player).sendMessage(ChatType.ACTION_BAR, ChatColor.GREEN + "look:" + (look == null ? "null" : look.getBlockType()));
        plugin.notifyOps("You are Op 22222 !!!!!!!!!!!");
    }
}
