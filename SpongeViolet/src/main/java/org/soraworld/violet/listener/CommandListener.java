package org.soraworld.violet.listener;

import org.soraworld.violet.api.IManager;
import org.soraworld.violet.inject.EventListener;
import org.soraworld.violet.inject.Inject;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.util.Tristate;

@EventListener
public class CommandListener {

    @Inject
    IManager manager;

    @Listener(order = Order.PRE)
    public void onServerCommand(ServerCommandEvent event) {
    }

    @IsCancelled(Tristate.UNDEFINED)
    @Listener(order = Order.PRE)
    public void onSendCommand(SendCommandEvent event) {
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
