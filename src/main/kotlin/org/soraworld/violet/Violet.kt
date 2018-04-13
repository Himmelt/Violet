package org.soraworld.violet

import org.soraworld.violet.command.CommandViolet
import org.soraworld.violet.command.IICommand
import org.soraworld.violet.config.IIConfig
import org.soraworld.violet.config.VioletConfig
import org.soraworld.violet.constant.Violets
import java.io.File

class Violet : VioletPlugin() {

    override fun registerConfig(path: File): IIConfig {
        val config = VioletConfig(path)
        staticConfig = config
        return config
    }

    override fun registerEvents() {
    }

    override fun registerCommand(): IICommand? {
        return CommandViolet(Violets.PLUGIN_ID, null, config)
    }

    override fun afterEnable() {

    }

    override fun beforeDisable() {

    }

    companion object {
        var staticConfig: IIConfig? = null
        fun translate(key: String, lang: String, vararg args: Any): String {
            return staticConfig?.formatLangKey(lang, key, args) ?: key
        }
    }

}
