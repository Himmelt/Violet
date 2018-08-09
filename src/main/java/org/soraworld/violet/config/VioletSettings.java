package org.soraworld.violet.config;

import org.soraworld.hocon.Setting;

public class VioletSettings {
    @Setting(comment = "comment.lang")
    public String lang = "zh_cn";
    @Setting(comment = "comment.debug")
    public boolean debug = false;
}
