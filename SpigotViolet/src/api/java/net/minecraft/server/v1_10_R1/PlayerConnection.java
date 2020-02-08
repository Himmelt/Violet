package net.minecraft.server.v1_10_R1;

/**
 * @author Himmelt
 */
public abstract class PlayerConnection {
    public abstract void sendPacket(final Packet packet);
}
