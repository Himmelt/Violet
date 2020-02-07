package org.soraworld.violet;

import org.bukkit.Bukkit;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.inject.Config;
import org.soraworld.violet.util.Reflects;
import org.soraworld.violet.version.McVersion;
import org.soraworld.violet.version.Version;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Violet 常量.
 *
 * @author Himmelt
 */
@Config(id = Violet.PLUGIN_ID, clazz = true)
public final class Violet {
    public static final String PLUGIN_ID = "violet";
    public static final String PLUGIN_NAME = "Violet";
    public static final String PLUGIN_VERSION = "2.5.0";

    public static final McVersion MC_VERSION;
    public static final boolean BUKKIT, SPONGE, ONLINE_MODE;
    public static final String SPONGE_IMPL;
    public static final Version VIOLET_VERSION = Version.parse(PLUGIN_VERSION);

    @Setting
    private static boolean enableStats = true;
    @Setting(path = "serverId")
    private static final UUID SERVER_UUID = UUID.randomUUID();

    static {
        String spongeImpl = "";
        boolean bukkit = false, sponge = false, onlineMode = false;
        try {
            Class<?> clazz = Class.forName("org.bukkit.Bukkit");
            bukkit = clazz != null;
        } catch (Throwable ignored) {
        }
        try {
            Class<?> clazz = Class.forName("org.spongepowered.api.Sponge");
            sponge = clazz != null;
        } catch (Throwable ignored) {
        }
        String version = "";
        if (bukkit) {
            version = Bukkit.getVersion();
            version = version.substring(version.indexOf("(MC:") + 4, version.length() - 1);
            onlineMode = Bukkit.getOnlineMode();
        } else if (sponge) {
            Object server = Sponge.getServer();
            try {
                Method getMcVersion = Reflects.getMethod(server.getClass(), "getMinecraftVersion", "func_71249_w", "B");
                version = (String) getMcVersion.invoke(server);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            onlineMode = Sponge.getServer().getOnlineMode();
            spongeImpl = Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getName();
        }
        BUKKIT = bukkit;
        SPONGE = sponge;
        ONLINE_MODE = onlineMode;
        SPONGE_IMPL = spongeImpl;
        MC_VERSION = McVersion.parse(version.trim());
    }

    public static boolean enableStats() {
        return enableStats;
    }

    public static UUID getServerId() {
        return SERVER_UUID;
    }
}
