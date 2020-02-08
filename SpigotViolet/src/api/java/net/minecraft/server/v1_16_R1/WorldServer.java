package net.minecraft.server.v1_16_R1;

/**
 * The type World server.
 *
 * @author Himmelt
 */
public abstract class WorldServer extends World {
    /**
     * Gets chunk provider.
     *
     * @return the chunk provider
     */
    public abstract ChunkProviderServer getChunkProvider();
}
