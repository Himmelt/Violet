package org.soraworld.violet.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.soraworld.violet.constant.Violets;

@ConfigSerializable
public abstract class Settings {

    @Setting(comment = "Language id")
    String lang = "zh_cn";
    @Setting(comment = "Debug Mode")
    boolean debug = false;
    @Setting(comment = "Version")
    String version = Violets.PLUGIN_VERSION;
    String adminPerm = "";

}
