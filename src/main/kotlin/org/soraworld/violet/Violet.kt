package org.soraworld.violet

import org.soraworld.violet.command.CommandViolet
import org.soraworld.violet.command.IICommand
import org.soraworld.violet.config.IIConfig
import org.soraworld.violet.config.VioletConfig
import org.soraworld.violet.constant.Violets
import java.io.File

class Violet : VioletPlugin() {

    override fun registerConfig(path: File): IIConfig {
        return VioletConfig(path)
    }

    override fun registerEvents() {}

    override fun registerCommand(): IICommand? {
        return CommandViolet(Violets.PLUGIN_ID, iconfig.adminPerm, iconfig)
    }

    override fun afterEnable() {}

    override fun beforeDisable() {}

}
