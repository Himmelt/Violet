package org.soraworld.violet.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.soraworld.violet.util.ListUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

public abstract class IICommand implements TabExecutor {

    private final String name;
    private final List<String> aliases;
    private final TreeMap<String, IICommand> subs = new TreeMap<>();

    IICommand(String name, String... aliases) {
        this.name = name;
        this.aliases = ListUtil.arrayList(aliases);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
        return execute(sender, ListUtil.arrayList(args));
    }

    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (args.size() >= 1) {
            IICommand sub = subs.get(args.remove(0));
            if (sub != null) {
                return sub.execute(sender, args);
            }
        }
        if (!getUsage().isEmpty()) {
            sender.sendMessage(getUsage());
        }
        return false;
    }

    @Nonnull
    private String getUsage() {
        return "";
    }

    final void addSub(IICommand sub) {
        this.subs.put(sub.name, sub);
        for (String alias : sub.aliases) {
            IICommand command = this.subs.get(alias);
            if (command == null || !command.name.equals(alias)) {
                this.subs.put(alias, sub);
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return getTabCompletions(sender, ListUtil.arrayList(args));
    }

    private List<String> getTabCompletions(CommandSender sender, ArrayList<String> args) {
        if (args.size() == 1) {
            return getMatchList(args.get(0), subs.keySet());
        } else if (args.size() >= 2) {
            IICommand sub = subs.get(args.remove(0));
            if (sub != null) return sub.getTabCompletions(sender, args);
            else return new ArrayList<>();
        } else {
            return new ArrayList<>(subs.keySet());
        }
    }

    private static List<String> getMatchList(String arg, Collection<String> possibles) {
        if (arg.isEmpty()) return new ArrayList<>(possibles);
        ArrayList<String> list = new ArrayList<>();
        for (String s : possibles) {
            if (s.startsWith(arg)) {
                list.add(s);
            }
        }
        return list;
    }

}
