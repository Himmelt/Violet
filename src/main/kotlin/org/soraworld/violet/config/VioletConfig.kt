package org.soraworld.violet.config

import org.bukkit.ChatColor
import org.soraworld.violet.constant.Violets
import java.io.File

class VioletConfig(path: File) : IIConfig(path) {

    init {
        vcfg = this
    }

    override val adminPerm: String = Violets.PERM_ADMIN

    override var plainHead: String = "[" + Violets.PLUGIN_NAME + "] "

    override var headColor: ChatColor = ChatColor.DARK_PURPLE

    override fun loadOptions() {}

    override fun saveOptions() {
        cfgYaml["version"] = Violets.PLUGIN_VERSION
    }

    override fun afterLoad() {
        consoleV(Violets.KEY_GET_VERSION, cfgYaml["version", Violets.PLUGIN_VERSION])
    }

}
