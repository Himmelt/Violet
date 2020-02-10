package org.soraworld.violettest;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.soraworld.violet.inject.Listener;
import org.soraworld.violet.nms.Helper;
import org.soraworld.violet.text.ChatColor;
import org.soraworld.violet.text.ChatType;
import org.soraworld.violet.wrapper.Wrapper;

/**
 * @author Himmelt
 */
@Listener
public class PlayerEventListener implements org.bukkit.event.Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block look = Helper.getLookAt(event.getPlayer(), 30);
        Wrapper.wrapper(event.getPlayer()).sendMessage(ChatType.ACTION_BAR, ChatColor.GREEN + "look:" + (look == null ? "null" : look.getType()));
    }
}
