package org.soraworld.violet;


import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.inject.Config;
import org.soraworld.violet.version.McVersion;

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
    // TODO hocon 增加对类静态字段的支持。修改对象是 class
    @Setting(path = "serverId")
    public static final UUID SERVER_UUID = UUID.randomUUID();

    static {
        McVersion version = new McVersion(1, 7, 10, 4, false, false);
        // TODO
        MC_VERSION = version;
    }
}
