package org.soraworld.violet;

import org.soraworld.violet.command.VioletCommand;
import org.soraworld.violet.config.Settings;
import org.soraworld.violet.config.VioletSettings;
import org.soraworld.violet.constant.Violets;
import org.soraworld.violet.plugin.VioletPlugin;
import rikka.api.command.IICommand;

public class Violet extends VioletPlugin {

    protected Settings regSettings() {
        return new VioletSettings();
    }

    protected IICommand regCommand() {
        return new VioletCommand(Violets.PERM_ADMIN, false, manager, Violets.PLUGIN_ID);
    }

    protected void afterEnable() {

    }

}
