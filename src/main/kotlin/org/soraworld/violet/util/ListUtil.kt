package org.soraworld.violet.util

import org.bukkit.Bukkit

object ListUtil {


    fun getMatchList(text: String, possibles: Collection<String>): MutableList<String> {
        if (text.isEmpty()) return possibles.toMutableList()
        val list = ArrayList<String>()
        for (s in possibles) {
            if (s.startsWith(text)) {
                list.add(s)
            }
        }
        return list
    }

    fun getMatchWorlds(text: String): List<String> {
        val list = ArrayList<String>()
        for (world in Bukkit.getWorlds()) {
            if (world.name.startsWith(text)) list.add(world.name)
        }
        return list
    }

    fun getMatchPlayers(text: String): List<String> {
        val list = ArrayList<String>()
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.name.startsWith(text)) list.add(player.name)
        }
        return list
    }

}
