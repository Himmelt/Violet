package net.minecraft.server.v1_8_R2;

/**
 * @author Himmelt
 */
public abstract class PlayerConnection {
    public abstract void sendPacket(final Packet packet);
}
