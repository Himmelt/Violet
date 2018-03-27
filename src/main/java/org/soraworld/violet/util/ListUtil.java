package org.soraworld.violet.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ListUtil {

    @SafeVarargs
    public static <T> ArrayList<T> arrayList(T... elements) {
        ArrayList<T> list = new ArrayList<>();
        Collections.addAll(list, elements);
        return list;
    }

    public static List<String> getMatchList(@Nonnull String text, Collection<String> possibles) {
        if (text.isEmpty()) return new ArrayList<>(possibles);
        ArrayList<String> list = new ArrayList<>();
        for (String s : possibles) {
            if (s.startsWith(text)) {
                list.add(s);
            }
        }
        return list;
    }

    public static List<String> getMatchWorlds(@Nonnull String text) {
        ArrayList<String> list = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().startsWith(text)) list.add(world.getName());
        }
        return list;
    }

    public static List<String> getMatchPlayers(@Nonnull String text) {
        ArrayList<String> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().startsWith(text)) list.add(player.getName());
        }
        return list;
    }

}
