package org.soraworld.violet.config

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import org.soraworld.violet.Violet
import org.soraworld.violet.chat.IIChat
import org.soraworld.violet.chat.IILang
import org.soraworld.violet.constant.Violets
import org.soraworld.violet.yaml.IYamlConfiguration
import java.io.File

abstract class IIConfig(path: File, val plugin: Plugin) {

    val iiLang: IILang
    val iiChat: IIChat

    protected var debug = false
    protected val config_file: File
    protected val config_yaml = IYamlConfiguration()

    var lang: String
        get() = iiLang.lang
        set(lang) {
            iiLang.lang = lang
            if (iiLang.hasKey(Violets.KEY_CHAT_HEAD)) iiChat.setHead(iiLang.format(Violets.KEY_CHAT_HEAD))
            else iiChat.setHead(defaultChatHead())
        }

    init {
        this.config_file = File(path, "config.yml")
        this.iiChat = IIChat(defaultChatHead(), defaultChatColor())
        this.iiLang = IILang(File(path, "lang"), this)
    }

    fun load(): Boolean {
        if (!config_file.exists()) {
            lang = config_yaml.getString("lang")
            save()
            return true
        }
        try {
            config_yaml.load(config_file)
            debug = config_yaml.getBoolean("debug")
            lang = config_yaml.getString("lang")
            loadOptions()
        } catch (e: Throwable) {
            if (debug) e.printStackTrace()
            iiChat.console("&cConfig file load exception !!!")
            return false
        }

        return true
    }

    fun save(): Boolean {
        try {
            config_yaml.clear()
            config_yaml.set("debug", debug)
            config_yaml.set("lang", iiLang.lang)
            saveOptions()
            config_yaml.save(config_file)
        } catch (e: Throwable) {
            if (debug) e.printStackTrace()
            iiChat.console("&cConfig file save exception !!!")
            return false
        }

        return true
    }

    fun debug(): Boolean {
        return debug
    }

    fun debug(debug: Boolean) {
        this.debug = debug
    }

    fun send(sender: CommandSender, key: String, vararg args: Any) {
        iiChat.send(sender, iiLang.format(key, *args))
    }

    fun broadcast(key: String, vararg args: Any) {
        iiChat.broadcast(iiLang.format(key, *args))
    }

    fun console(key: String, vararg args: Any) {
        iiChat.console(iiLang.format(key, *args))
    }

    fun println(message: String) {
        iiChat.console(message)
    }

    fun sendV(sender: CommandSender, key: String, vararg args: Any) {
        iiChat.send(sender, Violet.translate(lang, key, *args))
    }

    fun broadcastV(key: String, vararg args: Any) {
        iiChat.broadcast(Violet.translate(lang, key, *args))
    }

    fun consoleV(key: String, vararg args: Any) {
        iiChat.console(Violet.translate(lang, key, *args))
    }

    protected abstract fun loadOptions()

    protected abstract fun saveOptions()

    abstract fun afterLoad()

    protected abstract fun defaultChatColor(): ChatColor

    protected abstract fun defaultChatHead(): String

    abstract fun defaultAdminPerm(): String

}
