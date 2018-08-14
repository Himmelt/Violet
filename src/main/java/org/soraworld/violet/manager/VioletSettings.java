package org.soraworld.violet.manager;

import org.soraworld.hocon.node.Setting;

public class VioletSettings {
    @Setting(comment = "comment.lang")
    protected String lang = "zh_cn";
    @Setting(comment = "comment.debug")
    protected boolean debug = false;
}
