package org.soraworld.violet.config

import org.bukkit.ChatColor
import org.soraworld.violet.constant.Violets
import java.io.File

class VioletConfig(path: File) : IIConfig(path) {

    override fun loadOptions() {}

    override fun saveOptions() {}

    override fun afterLoad() {}

    override fun defaultChatColor(): ChatColor {
        return ChatColor.DARK_PURPLE
    }

    override fun defaultChatHead(): String {
        return "[" + Violets.PLUGIN_NAME + "] "
    }

    override fun defaultAdminPerm(): String {
        return Violets.PERM_ADMIN
    }

}
