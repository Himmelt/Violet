package org.soraworld.violet.version;

/**
 * @author Himmelt
 */
public final class NMS {
    public static final boolean v1_7_R4;
    public static final boolean v1_8_R1;
    public static final boolean v1_8_R3;
    public static final boolean v1_9_R1;
    public static final boolean v1_9_R2;
    public static final boolean v1_10_R1;
    public static final boolean v1_11_R1;
    public static final boolean v1_12_R1;
    public static final boolean v1_13_R1;
    public static final boolean v1_13_R2;
    public static final boolean v1_14_R1;
    public static final boolean v1_15_R1;

    static {
        boolean v1_7_r4 = false, v1_8_r1 = false, v1_8_r3 = false, v1_9_r1 = false, v1_9_r2 = false,
                v1_10_r1 = false, v1_11_r1 = false, v1_12_r1 = false, v1_13_r1 = false, v1_13_r2 = false,
                v1_14_r1 = false, v1_15_r1 = false;
        try {
            org.bukkit.craftbukkit.v1_7_R4.CraftServer.class.getName();
            net.minecraft.server.v1_7_R4.MinecraftServer.class.getName();
            v1_7_r4 = true;
        } catch (Throwable ignored) {
        }
        try {
            org.bukkit.craftbukkit.v1_8_R1.CraftServer.class.getName();
            net.minecraft.server.v1_8_R1.MinecraftServer.class.getName();
            v1_8_r1 = true;
        } catch (Throwable ignored) {
        }
        try {
            org.bukkit.craftbukkit.v1_8_R3.CraftServer.class.getName();
            net.minecraft.server.v1_8_R3.MinecraftServer.class.getName();
            v1_8_r3 = true;
        } catch (Throwable ignored) {
        }
        try {
            org.bukkit.craftbukkit.v1_9_R1.CraftServer.class.getName();
            net.minecraft.server.v1_9_R1.MinecraftServer.class.getName();
            v1_9_r1 = true;
        } catch (Throwable ignored) {
        }
        try {
            org.bukkit.craftbukkit.v1_9_R2.CraftServer.class.getName();
            net.minecraft.server.v1_9_R2.MinecraftServer.class.getName();
            v1_9_r2 = true;
        } catch (Throwable ignored) {
        }
        try {
            org.bukkit.craftbukkit.v1_10_R1.CraftServer.class.getName();
            net.minecraft.server.v1_10_R1.MinecraftServer.class.getName();
            v1_10_r1 = true;
        } catch (Throwable ignored) {
        }
        try {
            org.bukkit.craftbukkit.v1_11_R1.CraftServer.class.getName();
            net.minecraft.server.v1_11_R1.MinecraftServer.class.getName();
            v1_11_r1 = true;
        } catch (Throwable ignored) {
        }
        try {
            org.bukkit.craftbukkit.v1_12_R1.CraftServer.class.getName();
            net.minecraft.server.v1_12_R1.MinecraftServer.class.getName();
            v1_12_r1 = true;
        } catch (Throwable ignored) {
        }
        try {
            org.bukkit.craftbukkit.v1_13_R1.CraftServer.class.getName();
            net.minecraft.server.v1_13_R1.MinecraftServer.class.getName();
            v1_13_r1 = true;
        } catch (Throwable ignored) {
        }
        try {
            org.bukkit.craftbukkit.v1_13_R2.CraftServer.class.getName();
            net.minecraft.server.v1_13_R2.MinecraftServer.class.getName();
            v1_13_r2 = true;
        } catch (Throwable ignored) {
        }
        try {
            org.bukkit.craftbukkit.v1_14_R1.CraftServer.class.getName();
            net.minecraft.server.v1_14_R1.MinecraftServer.class.getName();
            v1_14_r1 = true;
        } catch (Throwable ignored) {
        }
        try {
            org.bukkit.craftbukkit.v1_15_R1.CraftServer.class.getName();
            net.minecraft.server.v1_15_R1.MinecraftServer.class.getName();
            v1_15_r1 = true;
        } catch (Throwable ignored) {
        }
        v1_7_R4 = v1_7_r4;
        v1_8_R1 = v1_8_r1;
        v1_8_R3 = v1_8_r3;
        v1_9_R1 = v1_9_r1;
        v1_9_R2 = v1_9_r2;
        v1_10_R1 = v1_10_r1;
        v1_11_R1 = v1_11_r1;
        v1_12_R1 = v1_12_r1;
        v1_13_R1 = v1_13_r1;
        v1_13_R2 = v1_13_r2;
        v1_14_R1 = v1_14_r1;
        v1_15_R1 = v1_15_r1;
    }
}
