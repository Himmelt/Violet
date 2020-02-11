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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + worldId.hashCode();
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BlockPos && worldId.equals(((BlockPos) obj).worldId) && x == ((BlockPos) obj).x && y == ((BlockPos) obj).y && z == ((BlockPos) obj).z;
    }
}
