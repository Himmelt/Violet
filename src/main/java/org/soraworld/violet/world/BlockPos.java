package org.soraworld.violet.world;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author Himmelt
 */
public final class BlockPos {
    private final UUID worldId;
    private final int x, y, z;

    public BlockPos(@NotNull UUID worldId, int x, int y, int z) {
        this.worldId = worldId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public UUID getWorldId() {
        return worldId;
    }
}
