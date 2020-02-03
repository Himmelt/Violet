package org.soraworld.violet;


import org.bukkit.Bukkit;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.inject.Config;
import org.soraworld.violet.util.Reflects;
import org.soraworld.violet.version.McVersion;
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
    // TODO hocon 增加对类静态字段的支持。修改对象是 class, 避免 final 常量优化
    @Setting(path = "serverId")
    public static final UUID SERVER_UUID = UUID.randomUUID();

    static {
        boolean bukkit = false, sponge = false;
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
            version = Bukkit.getServer().getVersion();
            version = version.substring(version.indexOf("(MC:") + 4, version.length() - 1);
        } else if (sponge) {
            Object server = Sponge.getServer();
            try {
                Method getMcVersion = Reflects.getMethod(server.getClass(), "getMinecraftVersion", "func_71249_w", "B");
                version = (String) getMcVersion.invoke(server);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        MC_VERSION = McVersion.parse(version.trim());
    }
}
