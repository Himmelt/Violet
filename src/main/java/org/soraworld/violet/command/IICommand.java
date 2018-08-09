package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class IICommand {

    private final String perm;
    private final boolean onlyPlayer;

    private final List<String> aliases = new ArrayList<>();
    private final HashMap<String, IICommand> subs = new LinkedHashMap<>();

    public IICommand(String perm, boolean onlyPlayer, String... aliases) {
        this.perm = perm;
        this.onlyPlayer = onlyPlayer;
        this.aliases.addAll(Arrays.asList(aliases));
    }

    public String getName() {
        return aliases.isEmpty() ? "emptyCmdName" : aliases.get(0);
    }

    public void execute(CommandSender sender, CommandArgs args) {
        if (args.notEmpty()) {
            IICommand sub = subs.get(args.first());
            if (sub != null) {
                if (sub.canRun(sender)) {
                    args.next();
                    if (sender instanceof Player) sub.execute((Player) sender, args);
                    else if (!onlyPlayer) sub.execute(sender, args);
                    //else TODO only player
                }//else TODO no permission
            }//else TODO not sub -> usage
        }//else TODO usage
    }

    public void execute(Player player, CommandArgs args) {
        execute((CommandSender) player, args);
    }

    public List<String> tabCompletions(CommandArgs args) {
        String first = args.first();
        if (args.size() == 1) return getMatchList(first, subs.keySet());
        if (subs.containsKey(first)) {
            args.next();
            return subs.get(first).tabCompletions(args);
        }
        return new ArrayList<>();
    }

    protected void addSub(IICommand sub) {
        for (String alias : sub.aliases) {
            subs.putIfAbsent(alias, sub);
        }
    }

    public boolean canRun(CommandSender sender) {
        return perm == null || sender.hasPermission(perm);
    }

    public static List<String> getMatchList(String text, Collection<String> possibles) {
        ArrayList<String> list = new ArrayList<>();
        if (text.isEmpty()) list.addAll(possibles);
        else for (String s : possibles) if (s.startsWith(text)) list.add(s);
        return list;
    }

}
