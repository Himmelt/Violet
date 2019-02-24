package org.soraworld.violet.listener;

import org.soraworld.violet.inject.EventListener;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.manager.VManager;
import org.soraworld.violet.util.OpUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;

@EventListener
public class UpdateListener {
    @Inject
    private VManager manager;

    @Listener(order = Order.POST)
    public void onClientConnectionJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        if (OpUtils.isOp(player)) {
            manager.checkUpdate(player);
        }
    }
}
