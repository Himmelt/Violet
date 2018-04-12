package org.soraworld.violet.chat

import org.soraworld.violet.config.IIConfig
import org.soraworld.violet.util.FileUtil
import org.soraworld.violet.yaml.IYamlConfiguration
import java.io.File

class IILang(private val path: File, private val config: IIConfig) {


    private var langFile: File? = null
    private val langYaml = IYamlConfiguration()
    var lang: String? = "en_us"
        set(lang) {
            field = if (lang == null || lang.isEmpty()) "en_us" else lang
            this.langFile = File(path, "$field.yml")
            load()
        }

    private fun load() {
        if (!langFile!!.exists()) {
            try {
                path.mkdirs()
                val input = config.javaClass.getResourceAsStream("/lang/$lang.yml")
                FileUtil.copyInputStreamToFile(input, langFile)
            } catch (e: Throwable) {
                if (config.debug()) e.printStackTrace()
                config.println("&cLang file $lang extract exception !!!")
                if ("en_us" != this.lang) {
                    lang = "en_us"
                    config.println("&cLang fall back to en_us .")
                }
            }
        }
        try {
            langYaml.load(langFile)
        } catch (e: Throwable) {
            if (config.debug()) e.printStackTrace()
            config.println("&cLang file $lang load exception !!!")
            if ("en_us" != this.lang) {
                lang = "en_us"
                config.println("&cLang fall back to en_us .")
            }
        }
    }

    fun hasKey(key: String): Boolean {
        return langYaml.getKeys(false).contains(key)
    }

    fun format(key: String, vararg args: Any): String {
        val value = langYaml.getString(key)
        return if (value == null) key else String.format(value, *args)
    }

}
