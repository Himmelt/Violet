package org.soraworld.violet.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.inject.EventListener;
import org.soraworld.violet.inject.Inject;

@EventListener
public class CommandListener implements Listener {

    @Inject
    IManager manager;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerCommand(ServerCommandEvent event) {
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String content = event.getBuffer();
        System.out.println(content);
    }

    @EventHandler
    public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent event) {
        String content = event.getChatMessage();
        System.out.println(content);
    }
}
