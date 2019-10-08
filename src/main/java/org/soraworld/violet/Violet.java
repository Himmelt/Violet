package org.soraworld.violet;


import org.soraworld.violet.lib.Library;

import java.util.ArrayList;

/**
 * Violet 常量.
 *
 * @author Himmelt
 */
public final class Violet {
    public static final String PLUGIN_ID = "violet";
    public static final String ASSETS_ID = "violet";
    public static final String PLUGIN_NAME = "Violet";
    public static final String PLUGIN_VERSION = "2.4.3";
    public static final String HOCON_VERSION = "1.2.1";
    public static final String ROCKSDB_VERSION = "6.2.2";

    public static final ArrayList<Library> LIBRARIES = new ArrayList<>();

    static {
        LIBRARIES.add(new Library("https://maven.aliyun.com/repository/public/org/rocksdb/rocksdbjni/6.2.2/rocksdbjni-6.2.2-win64.jar", "", "win64"));
        LIBRARIES.add(new Library("https://maven.aliyun.com/repository/public/org/soraworld/hocon/1.2.2/hocon-1.2.2.jar", "", "all"));
    }

}
