package org.soraworld.violet.command

import org.bukkit.command.CommandSender
import org.soraworld.violet.config.IIConfig
import org.soraworld.violet.constant.Violets

open class CommandViolet(name: String, perm: String?, config: IIConfig) : IICommand(name, perm, config) {

    init {
        addSub(object : IICommand("lang", config.adminPerm, config) {
            override fun execute(sender: CommandSender, args: MutableList<String>): Boolean {
                if (args.isEmpty()) {
                    config.sendV(sender, Violets.KEY_GET_LANG, config.lang)
                } else {
                    config.lang = args[0]
                    config.save()
                    config.sendV(sender, Violets.KEY_SET_LANG, config.lang)
                }
                return true
            }
        })
        addSub(object : IICommand("save", config.adminPerm, config) {
            override fun execute(sender: CommandSender, args: MutableList<String>): Boolean {
                if (config.save()) {
                    config.sendV(sender, Violets.KEY_CFG_SAVE)
                } else {
                    config.sendV(sender, Violets.KEY_CFG_SAVE_FAIL)
                }
                return true
            }
        })
        addSub(object : IICommand("debug", config.adminPerm, config) {
            override fun execute(sender: CommandSender, args: MutableList<String>): Boolean {
                if (config.debug) {
                    config.debug = false
                    config.sendV(sender, Violets.KEY_DEBUG_OFF)
                } else {
                    config.debug = true
                    config.sendV(sender, Violets.KEY_DEBUG_ON)
                }
                return true
            }
        })
        addSub(object : IICommand("reload", config.adminPerm, config) {
            override fun execute(sender: CommandSender, args: MutableList<String>): Boolean {
                if (config.load()) {
                    config.sendV(sender, Violets.KEY_CFG_LOAD)
                } else {
                    config.sendV(sender, Violets.KEY_CFG_LOAD_FAIL)
                }
                return true
            }
        })
    }

}
