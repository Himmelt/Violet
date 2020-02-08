package org.soraworld.violet.util;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * @author Himmelt
 */
public final class Helper {
    private static PermissionService service;

    public static boolean isOp(Player player) {
        if (service == null) {
            Sponge.getServiceManager().getRegistration(PermissionService.class).ifPresent(p -> service = p.getProvider());
        }
        return service != null && service.getUserSubjects().hasSubject(player.getIdentifier()).getNow(false);
    }

    @Nullable
    public static Location<World> getLookAt(Player player, double distance) {
        BlockRay<World> ray = BlockRay.from(player)
                .skipFilter(BlockRay.onlyAirFilter())
                .stopFilter(BlockRay.allFilter()).build();
        if (ray.hasNext()) {
            return ray.next().getLocation();
        } else {
            return null;
        }
    }
}
