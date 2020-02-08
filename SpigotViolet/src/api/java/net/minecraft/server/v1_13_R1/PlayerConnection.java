package net.minecraft.server.v1_13_R1;

/**
 * @author Himmelt
 */
public abstract class PlayerConnection {
    public abstract void sendPacket(final Packet packet);
}
