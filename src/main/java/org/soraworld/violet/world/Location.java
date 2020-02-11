package org.soraworld.violet.world;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.util.Maths;

import java.util.UUID;

/**
 * @author Himmelt
 */
public final class Location {
    private final UUID worldId;
    private final double x, y, z;

    public Location(@NotNull UUID worldId, double x, double y, double z) {
        this.worldId = worldId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public @NotNull BlockPos getBlock() {
        return new BlockPos(worldId, Maths.floor(x), Maths.floor(y), Maths.floor(z));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public UUID getWorldId() {
        return worldId;
    }
}
