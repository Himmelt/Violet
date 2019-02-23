package org.soraworld.violet.listener;

import org.soraworld.violet.data.DataAPI;
import org.soraworld.violet.inject.EventListener;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.manager.FManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.profile.GameProfile;

import java.util.UUID;

@EventListener
public class DataListener {

    @Inject
    private FManager manager;

    @Listener(beforeModifications = true, order = Order.PRE)
    public void onPlayerLoginPre(ClientConnectionEvent.Auth event, @First GameProfile profile) {
        UUID uuid = profile.getUniqueId();
        manager.asyncLoadData(uuid);
    }

    @IsCancelled
    @Listener(order = Order.LAST)
    public void onPlayerLoginLast(ClientConnectionEvent.Login event, @First GameProfile profile) {
        UUID uuid = profile.getUniqueId();
        manager.asyncSaveData(uuid, true);
    }

    @Listener(order = Order.LAST)
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event, @First GameProfile profile) {
        UUID uuid = profile.getUniqueId();
        DataAPI.clearTemp(uuid);
        manager.asyncSaveData(uuid, true);
    }
}
