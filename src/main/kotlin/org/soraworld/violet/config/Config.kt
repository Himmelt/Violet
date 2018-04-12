package org.soraworld.violet.config

import org.bukkit.ChatColor
import org.bukkit.plugin.Plugin
import org.soraworld.violet.chat.VLang
import org.soraworld.violet.constant.Violets
import java.io.File

class Config(path: File, plugin: Plugin) : IIConfig(path, plugin) {

    val vLang: VLang = VLang(File(path, "lang"), this)

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
