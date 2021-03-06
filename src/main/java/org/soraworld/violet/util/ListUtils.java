package org.soraworld.violet.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Himmelt
 */
public class ListUtils {
    @NotNull
    public static List<String> getMatchList(@NotNull String text, @NotNull Collection<String> possibles) {
        ArrayList<String> list = new ArrayList<>();
        if (text.isEmpty()) {
            list.addAll(possibles);
        } else {
            for (String s : possibles) {
                if (s.startsWith(text)) {
                    list.add(s);
                }
            }
        }
        return list;
    }

    @NotNull
    public static List<String> getMatchListIgnoreCase(@NotNull String text, @NotNull Collection<String> possibles) {
        ArrayList<String> list = new ArrayList<>();
        if (text.isEmpty()) {
            list.addAll(possibles);
        } else {
            for (String s : possibles) {
                if (s.toLowerCase().startsWith(text.toLowerCase())) {
                    list.add(s);
                }
            }
        }
        return list;
    }
}
