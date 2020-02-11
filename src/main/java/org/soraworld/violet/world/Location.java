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
        return obj instanceof Location && worldId.equals(((Location) obj).worldId) && x == ((Location) obj).x && y == ((Location) obj).y && z == ((Location) obj).z;
    }
}
