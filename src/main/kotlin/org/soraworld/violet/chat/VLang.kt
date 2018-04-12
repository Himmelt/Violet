package org.soraworld.violet.chat

import org.soraworld.violet.config.IIConfig
import org.soraworld.violet.util.FileUtil
import org.soraworld.violet.yaml.IYamlConfiguration
import java.io.File
import java.util.*

class VLang(private val path: File, private val config: IIConfig) {

    private val files = HashMap<String, File>()
    private val yamls = HashMap<String, IYamlConfiguration>()

    private fun getLangFile(lang: String): File {
        var file: File? = files[lang]
        if (file == null) {
            file = File(path, "$lang.yml")
            files[lang] = file
        }
        return file
    }

    private fun getLangYaml(lang: String): IYamlConfiguration {
        var yaml: IYamlConfiguration? = yamls[lang]
        if (yaml == null) {
            yaml = IYamlConfiguration()
            yamls[lang] = yaml
            load(lang)
        }
        return yaml
    }

    private fun load(lang: String) {
        val langFile = getLangFile(lang)
        if (!langFile.exists()) {
            try {
                path.mkdirs()
                val input = this.javaClass.getResourceAsStream("/lang/$lang.yml")
                FileUtil.copyInputStreamToFile(input, langFile)
            } catch (e: Throwable) {
                if (config.debug()) e.printStackTrace()
                config.println("&cLang file $lang extract exception !!!")
            }
        }
        try {
            getLangYaml(lang).load(langFile)
        } catch (e: Throwable) {
            if (config.debug()) e.printStackTrace()
            config.println("&cLang file $lang load exception !!!")
        }
    }

    fun format(lang: String, key: String, vararg args: Any): String {
        val value = getLangYaml(lang).getString(key)
        return if (value == null || value.isEmpty()) {
            if ("en_us" == lang) key else format("en_us", key, *args)
        } else {
            String.format(value, *args)
        }
    }

}
