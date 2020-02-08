package org.soraworld.violet.nms;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.text.ChatType;

import static org.soraworld.violet.Violet.MC_VERSION;
import static org.soraworld.violet.version.McVersion.v1_13;
import static org.soraworld.violet.version.McVersion.v1_7_10;

/**
 * @author Himmelt
 */
public class Helper {

    public static void sendChatPacket(Player player, ChatType type, String text) {
        if (MC_VERSION == v1_7_10) {
            player.sendMessage(text);
        } else if (MC_VERSION.matchCraft(1, 8, 1)) {
            net.minecraft.server.v1_8_R1.PlayerConnection connection = ((org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer) player).getHandle().playerConnection;
            if (connection != null) {
                net.minecraft.server.v1_8_R1.PacketPlayOutChat packet = new net.minecraft.server.v1_8_R1.PacketPlayOutChat(new net.minecraft.server.v1_8_R1.ChatComponentText(text), (byte) type.ordinal());
                // packet.components = components;
                connection.sendPacket(packet);
            }
        } else if (MC_VERSION.matchCraft(1, 8, 2)) {
            net.minecraft.server.v1_8_R2.PlayerConnection connection = ((org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer) player).getHandle().playerConnection;
            if (connection != null) {
                net.minecraft.server.v1_8_R2.PacketPlayOutChat packet = new net.minecraft.server.v1_8_R2.PacketPlayOutChat(new net.minecraft.server.v1_8_R2.ChatComponentText(text), (byte) type.ordinal());
                // packet.components = components;
                connection.sendPacket(packet);
            }
        } else if (MC_VERSION.matchCraft(1, 8, 3)) {
            net.minecraft.server.v1_8_R3.PlayerConnection connection = ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().playerConnection;
            if (connection != null) {
                net.minecraft.server.v1_8_R3.PacketPlayOutChat packet = new net.minecraft.server.v1_8_R3.PacketPlayOutChat(new net.minecraft.server.v1_8_R3.ChatComponentText(text), (byte) type.ordinal());
                // packet.components = components;
                connection.sendPacket(packet);
            }
        } else if (MC_VERSION.matchCraft(1, 9, 1)) {
            net.minecraft.server.v1_9_R1.PlayerConnection connection = ((org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer) player).getHandle().playerConnection;
            if (connection != null) {
                net.minecraft.server.v1_9_R1.PacketPlayOutChat packet = new net.minecraft.server.v1_9_R1.PacketPlayOutChat(new net.minecraft.server.v1_9_R1.ChatComponentText(text), (byte) type.ordinal());
                // packet.components = components;
                connection.sendPacket(packet);
            }
        } else if (MC_VERSION.matchCraft(1, 9, 2)) {
            net.minecraft.server.v1_9_R2.PlayerConnection connection = ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer) player).getHandle().playerConnection;
            if (connection != null) {
                net.minecraft.server.v1_9_R2.PacketPlayOutChat packet = new net.minecraft.server.v1_9_R2.PacketPlayOutChat(new net.minecraft.server.v1_9_R2.ChatComponentText(text), (byte) type.ordinal());
                // packet.components = components;
                connection.sendPacket(packet);
            }
        } else if (MC_VERSION.matchCraft(1, 10, 1)) {
            net.minecraft.server.v1_10_R1.PlayerConnection connection = ((org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer) player).getHandle().playerConnection;
            if (connection != null) {
                net.minecraft.server.v1_10_R1.PacketPlayOutChat packet = new net.minecraft.server.v1_10_R1.PacketPlayOutChat(new net.minecraft.server.v1_10_R1.ChatComponentText(text), (byte) type.ordinal());
                // packet.components = components;
                connection.sendPacket(packet);
            }
        } else if (MC_VERSION.matchCraft(1, 11, 1)) {
            net.minecraft.server.v1_11_R1.PlayerConnection connection = ((org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer) player).getHandle().playerConnection;
            if (connection != null) {
                net.minecraft.server.v1_11_R1.PacketPlayOutChat packet = new net.minecraft.server.v1_11_R1.PacketPlayOutChat(new net.minecraft.server.v1_11_R1.ChatComponentText(text), (byte) type.ordinal());
                // packet.components = components;
                connection.sendPacket(packet);
            }
        } else if (MC_VERSION.matchCraft(1, 12, 1)) {
            net.minecraft.server.v1_12_R1.PlayerConnection connection = ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player).getHandle().playerConnection;
            if (connection != null) {
                net.minecraft.server.v1_12_R1.PacketPlayOutChat packet = new net.minecraft.server.v1_12_R1.PacketPlayOutChat(new net.minecraft.server.v1_12_R1.ChatComponentText(text),
                        type == ChatType.ACTION_BAR ? net.minecraft.server.v1_12_R1.ChatMessageType.GAME_INFO : type == ChatType.SYSTEM ? net.minecraft.server.v1_12_R1.ChatMessageType.SYSTEM : net.minecraft.server.v1_12_R1.ChatMessageType.CHAT);
                // packet.components = components;
                connection.sendPacket(packet);
            }
        } else if (MC_VERSION.matchCraft(1, 13, 1)) {
            net.minecraft.server.v1_13_R1.PlayerConnection connection = ((org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer) player).getHandle().playerConnection;
            if (connection != null) {
                net.minecraft.server.v1_13_R1.PacketPlayOutChat packet = new net.minecraft.server.v1_13_R1.PacketPlayOutChat(new net.minecraft.server.v1_13_R1.ChatComponentText(text),
                        type == ChatType.ACTION_BAR ? net.minecraft.server.v1_13_R1.ChatMessageType.GAME_INFO : type == ChatType.SYSTEM ? net.minecraft.server.v1_13_R1.ChatMessageType.SYSTEM : net.minecraft.server.v1_13_R1.ChatMessageType.CHAT);
                // packet.components = components;
                connection.sendPacket(packet);
            }
        } else if (MC_VERSION.matchCraft(1, 13, 2)) {
            net.minecraft.server.v1_13_R2.PlayerConnection connection = ((org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer) player).getHandle().playerConnection;
            if (connection != null) {
                net.minecraft.server.v1_13_R2.PacketPlayOutChat packet = new net.minecraft.server.v1_13_R2.PacketPlayOutChat(new net.minecraft.server.v1_13_R2.ChatComponentText(text),
                        type == ChatType.ACTION_BAR ? net.minecraft.server.v1_13_R2.ChatMessageType.GAME_INFO : type == ChatType.SYSTEM ? net.minecraft.server.v1_13_R2.ChatMessageType.SYSTEM : net.minecraft.server.v1_13_R2.ChatMessageType.CHAT);
                // packet.components = components;
                connection.sendPacket(packet);
            }
        } else if (MC_VERSION.matchCraft(1, 14, 1)) {
            net.minecraft.server.v1_14_R1.PlayerConnection connection = ((org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer) player).getHandle().playerConnection;
            if (connection != null) {
                net.minecraft.server.v1_14_R1.PacketPlayOutChat packet = new net.minecraft.server.v1_14_R1.PacketPlayOutChat(new net.minecraft.server.v1_14_R1.ChatComponentText(text),
                        type == ChatType.ACTION_BAR ? net.minecraft.server.v1_14_R1.ChatMessageType.GAME_INFO : type == ChatType.SYSTEM ? net.minecraft.server.v1_14_R1.ChatMessageType.SYSTEM : net.minecraft.server.v1_14_R1.ChatMessageType.CHAT);
                // packet.components = components;
                connection.sendPacket(packet);
            }
        } else if (MC_VERSION.matchCraft(1, 15, 1)) {
            net.minecraft.server.v1_15_R1.PlayerConnection connection = ((org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer) player).getHandle().playerConnection;
            if (connection != null) {
                net.minecraft.server.v1_15_R1.PacketPlayOutChat packet = new net.minecraft.server.v1_15_R1.PacketPlayOutChat(new net.minecraft.server.v1_15_R1.ChatComponentText(text),
                        type == ChatType.ACTION_BAR ? net.minecraft.server.v1_15_R1.ChatMessageType.GAME_INFO : type == ChatType.SYSTEM ? net.minecraft.server.v1_15_R1.ChatMessageType.SYSTEM : net.minecraft.server.v1_15_R1.ChatMessageType.CHAT);
                // packet.components = components;
                connection.sendPacket(packet);
            }
        } else if (MC_VERSION.matchCraft(1, 16, 1)) {
            net.minecraft.server.v1_16_R1.PlayerConnection connection = ((org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer) player).getHandle().playerConnection;
            if (connection != null) {
                net.minecraft.server.v1_16_R1.PacketPlayOutChat packet = new net.minecraft.server.v1_16_R1.PacketPlayOutChat(new net.minecraft.server.v1_16_R1.ChatComponentText(text),
                        type == ChatType.ACTION_BAR ? net.minecraft.server.v1_16_R1.ChatMessageType.GAME_INFO : type == ChatType.SYSTEM ? net.minecraft.server.v1_16_R1.ChatMessageType.SYSTEM : net.minecraft.server.v1_16_R1.ChatMessageType.CHAT);
                // packet.components = components;
                connection.sendPacket(packet);
            }
        } else {
            player.sendMessage(text);
        }
    }

    @Nullable
    public static Block rayTraceBlock(@NotNull Location start, @NotNull Vector direction, double maxDistance) {
        World world = start.getWorld();
        if (world != null) {
            if (MC_VERSION.matchCraft(1, 7, 4)) {
                Vector dir = direction.clone().normalize().multiply(maxDistance);
                net.minecraft.server.v1_7_R4.Vec3D startPos = net.minecraft.server.v1_7_R4.Vec3D.a(start.getX(), start.getY(), start.getZ());
                net.minecraft.server.v1_7_R4.Vec3D endPos = net.minecraft.server.v1_7_R4.Vec3D.a(start.getX() + dir.getX(), start.getY() + dir.getY(), start.getZ() + dir.getZ());
                net.minecraft.server.v1_7_R4.MovingObjectPosition result = ((org.bukkit.craftbukkit.v1_7_R4.CraftWorld) world).getHandle().rayTrace(startPos, endPos, false, true, false);
                if (result != null) {
                    return new Location(world, result.b, result.c, result.d).getBlock();
                }
            } else if (MC_VERSION.matchCraft(1, 8, 1)) {
                Vector dir = direction.clone().normalize().multiply(maxDistance);
                net.minecraft.server.v1_8_R1.Vec3D startPos = new net.minecraft.server.v1_8_R1.Vec3D(start.getX(), start.getY(), start.getZ());
                net.minecraft.server.v1_8_R1.Vec3D endPos = new net.minecraft.server.v1_8_R1.Vec3D(start.getX() + dir.getX(), start.getY() + dir.getY(), start.getZ() + dir.getZ());
                net.minecraft.server.v1_8_R1.MovingObjectPosition result = ((org.bukkit.craftbukkit.v1_8_R1.CraftWorld) world).getHandle().rayTrace(startPos, endPos, false, true, false);
                net.minecraft.server.v1_8_R1.BlockPosition pos;
                if (result != null && (pos = result.a()) != null) {
                    return new Location(world, pos.getX(), pos.getY(), pos.getZ()).getBlock();
                }
            } else if (MC_VERSION.matchCraft(1, 8, 2)) {
                Vector dir = direction.clone().normalize().multiply(maxDistance);
                net.minecraft.server.v1_8_R2.Vec3D startPos = new net.minecraft.server.v1_8_R2.Vec3D(start.getX(), start.getY(), start.getZ());
                net.minecraft.server.v1_8_R2.Vec3D endPos = new net.minecraft.server.v1_8_R2.Vec3D(start.getX() + dir.getX(), start.getY() + dir.getY(), start.getZ() + dir.getZ());
                net.minecraft.server.v1_8_R2.MovingObjectPosition result = ((org.bukkit.craftbukkit.v1_8_R2.CraftWorld) world).getHandle().rayTrace(startPos, endPos, false, true, false);
                net.minecraft.server.v1_8_R2.BlockPosition pos;
                if (result != null && (pos = result.a()) != null) {
                    return new Location(world, pos.getX(), pos.getY(), pos.getZ()).getBlock();
                }
            } else if (MC_VERSION.matchCraft(1, 8, 3)) {
                Vector dir = direction.clone().normalize().multiply(maxDistance);
                net.minecraft.server.v1_8_R3.Vec3D startPos = new net.minecraft.server.v1_8_R3.Vec3D(start.getX(), start.getY(), start.getZ());
                net.minecraft.server.v1_8_R3.Vec3D endPos = new net.minecraft.server.v1_8_R3.Vec3D(start.getX() + dir.getX(), start.getY() + dir.getY(), start.getZ() + dir.getZ());
                net.minecraft.server.v1_8_R3.MovingObjectPosition result = ((org.bukkit.craftbukkit.v1_8_R3.CraftWorld) world).getHandle().rayTrace(startPos, endPos, false, true, false);
                net.minecraft.server.v1_8_R3.BlockPosition pos;
                if (result != null && (pos = result.a()) != null) {
                    return new Location(world, pos.getX(), pos.getY(), pos.getZ()).getBlock();
                }
            } else if (MC_VERSION.matchCraft(1, 9, 1)) {
                Vector dir = direction.clone().normalize().multiply(maxDistance);
                net.minecraft.server.v1_9_R1.Vec3D startPos = new net.minecraft.server.v1_9_R1.Vec3D(start.getX(), start.getY(), start.getZ());
                net.minecraft.server.v1_9_R1.Vec3D endPos = new net.minecraft.server.v1_9_R1.Vec3D(start.getX() + dir.getX(), start.getY() + dir.getY(), start.getZ() + dir.getZ());
                net.minecraft.server.v1_9_R1.MovingObjectPosition result = ((org.bukkit.craftbukkit.v1_9_R1.CraftWorld) world).getHandle().rayTrace(startPos, endPos, false, true, false);
                net.minecraft.server.v1_9_R1.BlockPosition pos;
                if (result != null && (pos = result.a()) != null) {
                    return new Location(world, pos.getX(), pos.getY(), pos.getZ()).getBlock();
                }
            } else if (MC_VERSION.matchCraft(1, 9, 2)) {
                Vector dir = direction.clone().normalize().multiply(maxDistance);
                net.minecraft.server.v1_9_R2.Vec3D startPos = new net.minecraft.server.v1_9_R2.Vec3D(start.getX(), start.getY(), start.getZ());
                net.minecraft.server.v1_9_R2.Vec3D endPos = new net.minecraft.server.v1_9_R2.Vec3D(start.getX() + dir.getX(), start.getY() + dir.getY(), start.getZ() + dir.getZ());
                net.minecraft.server.v1_9_R2.MovingObjectPosition result = ((org.bukkit.craftbukkit.v1_9_R2.CraftWorld) world).getHandle().rayTrace(startPos, endPos, false, true, false);
                net.minecraft.server.v1_9_R2.BlockPosition pos;
                if (result != null && (pos = result.a()) != null) {
                    return new Location(world, pos.getX(), pos.getY(), pos.getZ()).getBlock();
                }
            } else if (MC_VERSION.matchCraft(1, 10, 1)) {
                Vector dir = direction.clone().normalize().multiply(maxDistance);
                net.minecraft.server.v1_10_R1.Vec3D startPos = new net.minecraft.server.v1_10_R1.Vec3D(start.getX(), start.getY(), start.getZ());
                net.minecraft.server.v1_10_R1.Vec3D endPos = new net.minecraft.server.v1_10_R1.Vec3D(start.getX() + dir.getX(), start.getY() + dir.getY(), start.getZ() + dir.getZ());
                net.minecraft.server.v1_10_R1.MovingObjectPosition result = ((org.bukkit.craftbukkit.v1_10_R1.CraftWorld) world).getHandle().rayTrace(startPos, endPos, false, true, false);
                net.minecraft.server.v1_10_R1.BlockPosition pos;
                if (result != null && (pos = result.a()) != null) {
                    return new Location(world, pos.getX(), pos.getY(), pos.getZ()).getBlock();
                }
            } else if (MC_VERSION.matchCraft(1, 11, 1)) {
                Vector dir = direction.clone().normalize().multiply(maxDistance);
                net.minecraft.server.v1_11_R1.Vec3D startPos = new net.minecraft.server.v1_11_R1.Vec3D(start.getX(), start.getY(), start.getZ());
                net.minecraft.server.v1_11_R1.Vec3D endPos = new net.minecraft.server.v1_11_R1.Vec3D(start.getX() + dir.getX(), start.getY() + dir.getY(), start.getZ() + dir.getZ());
                net.minecraft.server.v1_11_R1.MovingObjectPosition result = ((org.bukkit.craftbukkit.v1_11_R1.CraftWorld) world).getHandle().rayTrace(startPos, endPos, false, true, false);
                net.minecraft.server.v1_11_R1.BlockPosition pos;
                if (result != null && (pos = result.a()) != null) {
                    return new Location(world, pos.getX(), pos.getY(), pos.getZ()).getBlock();
                }
            } else if (MC_VERSION.matchCraft(1, 12, 1)) {
                Vector dir = direction.clone().normalize().multiply(maxDistance);
                net.minecraft.server.v1_12_R1.Vec3D startPos = new net.minecraft.server.v1_12_R1.Vec3D(start.getX(), start.getY(), start.getZ());
                net.minecraft.server.v1_12_R1.Vec3D endPos = new net.minecraft.server.v1_12_R1.Vec3D(start.getX() + dir.getX(), start.getY() + dir.getY(), start.getZ() + dir.getZ());
                net.minecraft.server.v1_12_R1.MovingObjectPosition result = ((org.bukkit.craftbukkit.v1_12_R1.CraftWorld) world).getHandle().rayTrace(startPos, endPos, false, true, false);
                net.minecraft.server.v1_12_R1.BlockPosition pos;
                if (result != null && (pos = result.a()) != null) {
                    return new Location(world, pos.getX(), pos.getY(), pos.getZ()).getBlock();
                }
            } else if (MC_VERSION.higherEquals(v1_13)) {
                RayTraceResult result = world.rayTraceBlocks(start, direction, maxDistance);
                return result != null ? result.getHitBlock() : null;
            }
        }
        return null;
    }

    @Nullable
    public static Block getLookAt(@NotNull Player player, double distance) {
        if (MC_VERSION.lower(v1_13)) {
            Location eyeLocation = player.getEyeLocation();
            Vector direction = eyeLocation.getDirection();
            return rayTraceBlock(eyeLocation, direction, distance);
        } else {
            RayTraceResult result = player.rayTraceBlocks(distance, FluidCollisionMode.NEVER);
            return result == null ? null : result.getHitBlock();
        }
    }
}
