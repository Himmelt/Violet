package org.soraworld.violet.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.soraworld.violet.config.IIConfig
import org.soraworld.violet.constant.Violets
import org.soraworld.violet.util.ListUtil
import java.util.*

abstract class IICommand(private val name: String, private val perm: String?, private val config: IIConfig, private val onlyPlayer: Boolean, vararg aliases: String) {

    private val subs = TreeMap<String, IICommand>()
    private val aliases: List<String> = aliases.asList()

    constructor(name: String, config: IIConfig, vararg aliases: String) : this(name, null, config, false, *aliases)

    constructor(name: String, perm: String, config: IIConfig, vararg aliases: String) : this(name, perm, config, false, *aliases)

    open fun execute(sender: CommandSender, args: MutableList<String>): Boolean {
        if (args.isEmpty()) return false
        val sub = subs[args.removeAt(0)] ?: return false
        if (sub.canRun(sender)) {
            when {
                sender is Player -> sub.execute(sender, args)
                sub.onlyPlayer -> config.sendV(sender, Violets.KEY_ONLY_PLAYER)
                else -> sub.execute(sender, args)
            }
        } else config.sendV(sender, Violets.KEY_NO_CMD_PERM, sub.perm)
        return true
    }

    open fun execute(player: Player, args: MutableList<String>): Boolean {
        return execute(player as CommandSender, args)
    }

    protected fun addSub(sub: IICommand) {
        this.subs[sub.name] = sub
        for (alias in sub.aliases) {
            val command = this.subs[alias]
            if (command == null || command.name != alias) this.subs[alias] = sub
        }
    }

    fun getTabCompletions(args: MutableList<String>?): MutableList<String> {
        return args?.let {
            when {
                it.size == 1 -> ListUtil.getMatchList(it[0], subs.keys)
                else -> subs[it.removeAt(0)]?.getTabCompletions(it)
            }
        } ?: ArrayList()
    }

    private fun canRun(sender: CommandSender): Boolean {
        return perm == null || sender.hasPermission(perm)
    }

}
