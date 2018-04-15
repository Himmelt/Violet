package org.soraworld.violet

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.soraworld.violet.command.IICommand
import org.soraworld.violet.config.IIConfig
import java.io.File

abstract class VioletPlugin : JavaPlugin() {

    protected lateinit var iconfig: IIConfig

    private var command: IICommand? = null

    override fun onEnable() {
        iconfig = registerConfig(dataFolder)
        iconfig.load()
        iconfig.afterLoad()
        command = registerCommand()
        registerEvents()
        afterEnable()
    }

    override fun onDisable() {
        beforeDisable()
        iconfig.save()
    }

    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        return sender?.let { this.command?.execute(it, args?.toMutableList() ?: ArrayList()) } ?: false
    }

    override fun onTabComplete(sender: CommandSender?, command: Command?, alias: String?, args: Array<out String>?): MutableList<String> {
        return this.command?.tabCompletions(args?.toMutableList() ?: ArrayList()) ?: ArrayList()
    }

    protected fun registerEvent(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }

    protected abstract fun registerConfig(path: File): IIConfig

    protected abstract fun registerCommand(): IICommand?

    protected abstract fun registerEvents()

    protected abstract fun afterEnable()

    protected abstract fun beforeDisable()

}
