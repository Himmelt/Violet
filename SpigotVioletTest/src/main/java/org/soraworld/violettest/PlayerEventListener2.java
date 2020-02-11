package org.soraworld.violettest;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.inject.InjectListener;
import org.soraworld.violet.inject.McVer;
import org.soraworld.violet.text.ChatColor;
import org.soraworld.violet.text.ChatType;
import org.soraworld.violet.util.Helper;
import org.soraworld.violet.wrapper.Wrapper;

/**
 * @author Himmelt
 */
@InjectListener
@McVer("[1.7.10,1.10.2]")
public class PlayerEventListener2 implements Listener {

    @Inject
    private IPlugin plugin;

    static {
        System.out.println("Test @McVer(\"[1.7.10,1.10.2]\")");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block look = Helper.getLookAt(event.getPlayer(), 30);
        Wrapper.wrapper(event.getPlayer()).sendMessage(ChatType.ACTION_BAR, ChatColor.GREEN + "look:" + (look == null ? "null" : look.getType()));
        plugin.notifyOps("You are Op 22222 !!!!!!!!!!!");
    }
}
