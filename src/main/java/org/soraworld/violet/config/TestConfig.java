package org.soraworld.violet.config;

import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.api.IConfig;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.core.PluginCore;
import org.soraworld.violet.inject.Config;
import org.soraworld.violet.inject.Inject;

/**
 * @author Himmelt
 */
@Config(id = "test")
public class TestConfig implements IConfig {

    @Setting
    private boolean testBool = false;
    @Setting
    private String testStr = "test Str ??";

    @Inject
    private IPlugin plugin;
    @Inject
    private PluginCore core;


}
