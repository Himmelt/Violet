package org.soraworld.violet.util;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.PermissionService;

public class OpUtils {
    private static PermissionService service;

    public static boolean isOp(Player player) {
        if (service == null) Sponge.getServiceManager().getRegistration(PermissionService.class).ifPresent(p -> service = p.getProvider());
        return service != null && service.getUserSubjects().hasSubject(player.getIdentifier()).getNow(false);
    }
}
