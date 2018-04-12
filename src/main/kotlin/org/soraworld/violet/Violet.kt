package org.soraworld.violet

import org.bukkit.event.Listener
import org.soraworld.violet.chat.VLang
import org.soraworld.violet.command.CommandViolet
import org.soraworld.violet.command.IICommand
import org.soraworld.violet.config.Config
import org.soraworld.violet.config.IIConfig
import org.soraworld.violet.constant.Violets
import java.io.File
import java.util.*

class Violet : VioletPlugin() {

    override fun registerConfig(path: File): IIConfig {
        val config = Config(path, this)
        vLang = config.vLang
        return config
    }

    override fun registerEvents(config: IIConfig): List<Listener> {
        return ArrayList()
    }

    override fun registerCommand(config: IIConfig): IICommand? {
        return CommandViolet(Violets.PLUGIN_ID, null, config, this)
    }

    override fun afterEnable() {

    }

    override fun beforeDisable() {

    }

    companion object {

        private var vLang: VLang? = null

        fun translate(lang: String, key: String, vararg args: Any): String {
            return if (vLang == null) key else vLang!!.format(lang, key, *args)
        }
    }

}
