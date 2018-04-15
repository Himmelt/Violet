package org.soraworld.violet.config

import net.minecraft.server.v1_7_R4.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.MemorySection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.file.YamlRepresenter
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
import org.bukkit.entity.Player
import org.soraworld.violet.constant.Violets
import org.soraworld.violet.util.FileUtil
import org.soraworld.violet.yaml.IEmitter
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.error.YAMLException
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.resolver.Resolver
import org.yaml.snakeyaml.serializer.Serializer
import java.io.*
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*
import java.util.regex.Pattern

abstract class IIConfig(path: File) {

    /*
    * IIConfig Properties
    * */
    private val cfgYaml: YamlConfiguration = YamlConfiguration()
    private val cfgFile: File = File(path, "config.yml")
    abstract val adminPerm: String
    var debug: Boolean = false
    var lang: String = "en_us"
        set(lang) {
            field = lang
            setHead(formatKey(Violets.KEY_CHAT_HEAD))
        }

    /*
    * IILang Properties
    * */
    private val langPath: File = File(path, "lang")
    private val langFiles: HashMap<String, File> = HashMap()
    private val langYamls: HashMap<String, YamlConfiguration> = HashMap()

    /*
    * IIChat Properties
    * */
    private var colorHead: String = ""
    private var styleHead: IChatBaseComponent = ChatComponentText("")
    abstract val headColor: ChatColor
    abstract var plainHead: String


    /*
    * IIConfig Methods
    * */

    fun load(): Boolean {
        vcfg = this
        if (!cfgFile.exists()) {
            lang = cfgYaml.getString("lang", "en_us")
            save()
            return true
        }
        try {
            cfgYaml.loadFile(cfgFile)
            debug = cfgYaml.getBoolean("debug", false)
            lang = cfgYaml.getString("lang", "en_us")
            loadOptions()
        } catch (e: Throwable) {
            if (debug) e.printStackTrace()
            consoleK("&cConfig file load exception !!!")
            return false
        }
        return true
    }

    fun save(): Boolean {
        try {
            cfgYaml.clear()
            cfgYaml.set("lang", lang)
            cfgYaml.set("debug", debug)
            saveOptions()
            cfgYaml.saveFile(cfgFile)
        } catch (e: Throwable) {
            if (debug) e.printStackTrace()
            consoleK("&cConfig file save exception !!!")
            return false
        }
        return true
    }


    /*
    * IIConfig Abstract Methods
    * */

    fun get(path: String, def: Any): Any {
        return cfgYaml[path, def]
    }

    fun set(path: String, value: Any) {
        cfgYaml[path] = value
    }

    protected abstract fun loadOptions()

    protected abstract fun saveOptions()

    abstract fun afterLoad()


    /*
    * IILang Methods
    * */

    private fun langFile(lang: String): File {
        var file: File? = langFiles[lang]
        if (file == null) {
            file = File(langPath, "$lang.yml")
            langFiles[lang] = file
        }
        return file
    }

    private fun langYaml(lang: String): YamlConfiguration {
        var yaml: YamlConfiguration? = langYamls[lang]
        if (yaml == null) {
            yaml = YamlConfiguration()
            langYamls[lang] = yaml
            loadLang(lang)
        }
        return yaml
    }

    private fun loadLang(lang: String) {
        val file = langFile(lang)
        if (!file.exists()) {
            try {
                langPath.mkdirs()
                val input = this.javaClass.getResourceAsStream("/lang/$lang.yml")
                FileUtil.copyInputStreamToFile(input, file)
            } catch (e: Throwable) {
                if (debug) e.printStackTrace()
                println("&cLang file $lang extract exception !!!")
            }
        }
        try {
            langYaml(lang).load(file)
        } catch (e: Throwable) {
            if (debug) e.printStackTrace()
            println("&cLang file $lang loadLang exception !!!")
        }
    }

    @JvmOverloads
    fun hasKey(key: String, lang: String = this.lang): Boolean {
        return langYaml(lang).getKeys(false).contains(key)
    }

    fun formatKey(key: String, vararg args: Any): String {
        return formatLangKey(lang, key, *args)
    }

    fun formatLangKey(lang: String, key: String, vararg args: Any): String {
        val value = langYaml(lang).getString(key)
        return if (value == null || value.isBlank())
            if (lang == "en_us") key else formatLangKey("en_us", key, *args)
        else String.format(value, *args)
    }


    /*
    * IIChat Methods
    * */

    fun setHead(text: String) {
        if (text.isNotBlank()) {
            colorHead = headColor.toString() + text.replace('&', ChatColor.COLOR_CHAR) + ChatColor.RESET
            styleHead = formatStyle(headColor.toString() + text)
            plainHead = styleHead.c()
        }
    }

    fun send(sender: CommandSender, key: String, vararg args: Any) {
        sender.sendMessage(colorHead + colorize(formatKey(key, *args)))
    }

    fun sendMessage(player: Player, vararg siblings: IChatBaseComponent) {
        if (player is CraftPlayer) {
            val message = styleHead.f()
            for (component in siblings) message.addSibling(component)
            player.handle.b(message)
        }
    }

    fun broadcast(key: String, vararg args: Any) {
        Bukkit.broadcastMessage(colorHead + colorize(formatKey(key, *args)))
    }

    fun console(message: String) {
        Bukkit.getConsoleSender().sendMessage(colorHead + colorize(message))
    }

    fun consoleK(key: String, vararg args: Any) {
        console(formatKey(key, *args))
    }

    fun println(message: String) {
        System.out.println(plainHead + message)
    }

    fun sendV(sender: CommandSender, key: String, vararg args: Any) {
        sender.sendMessage(colorHead + colorize(translate(lang, key, *args)))
    }

    fun broadcastV(key: String, vararg args: Any) {
        Bukkit.broadcastMessage(colorHead + colorize(translate(lang, key, *args)))
    }

    fun consoleV(key: String, vararg args: Any) {
        console(translate(lang, key, *args))
    }


    /*
    * Private Static Methods
    * */

    companion object {

        private val fieldMap: Field?
        private val methodHead: Method?
        private val resolver: Resolver = Resolver()
        private val dumperOptions: DumperOptions = DumperOptions()
        private val yamlRepresent: YamlRepresenter = YamlRepresenter()
        private const val BLANK_CONFIG = "{}\n"

        init {
            var map: Field? = null
            var head: Method? = null
            try {
                map = MemorySection::class.java.getDeclaredField("map")
                head = FileConfiguration::class.java.getDeclaredMethod("buildHeader")
                map?.isAccessible = true
                head?.isAccessible = true
            } catch (e: Throwable) {
                println("reflect failed !!!!!")
            }
            fieldMap = map
            methodHead = head
            dumperOptions.isAllowUnicode = true
            dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
            yamlRepresent.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        }

        private fun YamlConfiguration.saveToUTF8(): String {
            dumperOptions.indent = options().indent()
            val header = methodHead?.invoke(this) ?: ""
            var dump: String = dumps(getValues(false))
            if (dump == BLANK_CONFIG) dump = ""
            return if (header is String) header + dump else dump
        }

        fun YamlConfiguration.loadFile(file: File) {
            load(InputStreamReader(FileInputStream(file), Charsets.UTF_8))
        }

        fun YamlConfiguration.saveFile(file: File) {
            file.canonicalFile.parentFile.mkdirs()
            OutputStreamWriter(FileOutputStream(file), Charsets.UTF_8).use { writer ->
                writer.write(saveToUTF8())
            }
        }

        fun YamlConfiguration.clear() {
            fieldMap?.set(this, LinkedHashMap<String, Any>())
        }

        private fun dumps(data: Any): String {
            val list = ArrayList<Any>(1)
            list.add(data)
            val buffer = StringWriter()
            dumpAll(list.iterator(), buffer, dumperOptions.explicitRoot)
            return buffer.toString()
        }

        private fun dumpAll(data: Iterator<Any>, output: Writer, rootTag: Tag?) {
            val serializer = Serializer(IEmitter(output, dumperOptions), resolver, dumperOptions, rootTag)
            try {
                serializer.open()
                while (data.hasNext()) {
                    val node = yamlRepresent.represent(data.next())
                    serializer.serialize(node)
                }
                serializer.close()
            } catch (e: Throwable) {
                throw YAMLException(e)
            }
        }

        /*
        * =======================
        * */
        private var vcfg: IIConfig? = null
        private val FORMAT: Pattern = Pattern.compile("((?<!&)&[0-9a-fk-or])+")

        private fun parseStyle(text: String): ChatModifier {
            var style = ChatModifier()
            var i = 1
            while (i < text.length) {
                when (text[i]) {
                    '0' -> style.setColor(EnumChatFormat.BLACK)
                    '1' -> style.setColor(EnumChatFormat.DARK_BLUE)
                    '2' -> style.setColor(EnumChatFormat.DARK_GREEN)
                    '3' -> style.setColor(EnumChatFormat.DARK_AQUA)
                    '4' -> style.setColor(EnumChatFormat.DARK_RED)
                    '5' -> style.setColor(EnumChatFormat.DARK_PURPLE)
                    '6' -> style.setColor(EnumChatFormat.GOLD)
                    '7' -> style.setColor(EnumChatFormat.GRAY)
                    '8' -> style.setColor(EnumChatFormat.DARK_GRAY)
                    '9' -> style.setColor(EnumChatFormat.BLUE)
                    'a' -> style.setColor(EnumChatFormat.GREEN)
                    'b' -> style.setColor(EnumChatFormat.AQUA)
                    'c' -> style.setColor(EnumChatFormat.RED)
                    'd' -> style.setColor(EnumChatFormat.LIGHT_PURPLE)
                    'e' -> style.setColor(EnumChatFormat.YELLOW)
                    'f' -> style.setColor(EnumChatFormat.WHITE)
                    'k' -> style.setRandom(true)
                    'l' -> style.setBold(true)
                    'm' -> style.setStrikethrough(true)
                    'n' -> style.setUnderline(true)
                    'o' -> style.setItalic(true)
                    else -> style = ChatModifier()
                }
                i += 2
            }
            return style
        }

        @JvmOverloads
        fun formatStyle(text: String, ca: EnumClickAction? = null, cv: String? = null, ha: EnumHoverAction? = null, hv: String? = null): IChatBaseComponent {
            val matcher = FORMAT.matcher(text)
            val component = ChatComponentText("")
            var head = 0
            var style = ChatModifier()
            while (matcher.find()) {
                component.addSibling(ChatComponentText(text.substring(head, matcher.start()).replace("&&".toRegex(), "&")).setChatModifier(style))
                style = parseStyle(matcher.group())
                head = matcher.end()
            }
            component.addSibling(ChatComponentText(text.substring(head).replace("&&".toRegex(), "&")).setChatModifier(style))
            if (ca != null && cv != null) {
                component.chatModifier.setChatClickable(ChatClickable(ca, cv))
            }
            if (ha != null && hv != null) {
                component.chatModifier.a(ChatHoverable(ha, formatStyle(hv)))
            }
            return component
        }

        private fun colorize(message: String): String {
            return message.replace('&', ChatColor.COLOR_CHAR)
        }

        fun translate(lang: String, key: String, vararg args: Any): String {
            return vcfg?.formatLangKey(lang, key, *args) ?: key
        }

    }

}
