package org.soraworld.violet

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.soraworld.violet.command.IICommand
import org.soraworld.violet.config.IIConfig
import java.io.File

abstract class VioletPlugin : JavaPlugin() {

    protected lateinit var config: IIConfig
    private var command: IICommand? = null

    override fun onEnable() {
        config = registerConfig(dataFolder)
        config.load()
        config.afterLoad()
        for (listener in registerEvents()) server.pluginManager.registerEvents(listener, this)
        command = registerCommand()
        afterEnable()
    }

    override fun onDisable() {
        beforeDisable()
        config.save()
    }

    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        return sender?.let { this.command?.execute(it, args?.toMutableList() ?: ArrayList()) } ?: false
    }

    override fun onTabComplete(sender: CommandSender?, command: Command?, alias: String?, args: Array<out String>?): MutableList<String> {
        return this.command?.getTabCompletions(args?.toMutableList()) ?: ArrayList()
    }

    protected abstract fun registerConfig(path: File): IIConfig

    protected abstract fun registerEvents(): List<Listener>

    protected abstract fun registerCommand(): IICommand?

    protected abstract fun afterEnable()

    protected abstract fun beforeDisable()

}
