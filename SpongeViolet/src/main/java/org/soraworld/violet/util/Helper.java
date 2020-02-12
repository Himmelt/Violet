package org.soraworld.violet.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.text.ChatType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * @author Himmelt
 */
public final class Helper {

    private static PermissionService service;

    private Helper() {
    }

    public static boolean isOp(@NotNull Player player) {
        if (service == null) {
            Sponge.getServiceManager().getRegistration(PermissionService.class).ifPresent(p -> service = p.getProvider());
        }
        return service != null && service.getUserSubjects().hasSubject(player.getIdentifier()).getNow(false);
    }

    @Nullable
    public static Location<World> getLookAt(@NotNull Player player, double distance) {
        BlockRay<World> ray = BlockRay.from(player)
                .skipFilter(BlockRay.onlyAirFilter())
                .stopFilter(BlockRay.allFilter()).build();
        if (ray.hasNext()) {
            return ray.next().getLocation();
        } else {
            return null;
        }
    }

    public static void sendChatType(@NotNull Player source, @NotNull ChatType type, @NotNull String message) {
        switch (type) {
            case ACTION_BAR:
                source.sendMessage(ChatTypes.ACTION_BAR, Text.of(message));
                break;
            case SYSTEM:
                source.sendMessage(ChatTypes.SYSTEM, Text.of(message));
                break;
            default:
                source.sendMessage(ChatTypes.CHAT, Text.of(message));
        }
    }
}
