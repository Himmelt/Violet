package org.soraworld.violet.api;

import org.soraworld.violet.command.CommandArgs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface ICommand {

    String getName();

    boolean notOnlyPlayer();

    String getUsage();

    List<String> tabCompletions(CommandArgs args);

    static List<String> getMatchList(String text, Collection<String> possibles) {
        ArrayList<String> list = new ArrayList<>();
        if (text.isEmpty()) list.addAll(possibles);
        else for (String s : possibles) if (s.startsWith(text)) list.add(s);
        return list;
    }
}
