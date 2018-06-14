package org.soraworld.violet.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.soraworld.violet.constant.Violets;

@ConfigSerializable
public class VioletSetting {
    @Setting(comment = "Language id")
    public String lang = "zh_cn";
    @Setting(comment = "Debug Mode")
    public boolean debug = false;
    @Setting(comment = "Version")
    public String version = Violets.PLUGIN_VERSION;
}
