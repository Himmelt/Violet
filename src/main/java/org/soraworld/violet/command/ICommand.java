package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.soraworld.violet.Violets;
import org.soraworld.violet.api.IManager;

import java.util.*;

import static org.soraworld.violet.Violets.KEY_CMD_USAGE;

public abstract class ICommand {

    private final String perm;
    private final boolean onlyPlayer;
    protected final IManager manager;

    private final List<String> aliases = new ArrayList<>();
    private final HashMap<String, ICommand> subs = new LinkedHashMap<>();

    public ICommand(String perm, boolean onlyPlayer, IManager manager, String... aliases) {
        this.perm = perm;
        this.manager = manager;
        this.onlyPlayer = onlyPlayer;
        this.aliases.addAll(Arrays.asList(aliases));
    }

    public String getName() {
        return aliases.isEmpty() ? "emptyCmdName" : aliases.get(0);
    }

    public void execute(CommandSender sender, CommandArgs args) {
        if (args.notEmpty()) {
            ICommand sub = subs.get(args.first());
            if (sub != null) {
                if (sub.canRun(sender)) {
                    args.next();
                    if (sender instanceof Player) sub.execute((Player) sender, args);
                    else if (!sub.onlyPlayer) sub.execute(sender, args);
                    else manager.sendKey(sender, Violets.KEY_ONLY_PLAYER);
                } else manager.sendKey(sender, Violets.KEY_NO_CMD_PERM);
            } else sendUsage(sender);
        } else sendUsage(sender);
    }

    protected void sendUsage(CommandSender sender) {
        String usage = getUsage();
        if (usage != null && !usage.isEmpty()) {
            manager.sendKey(sender, KEY_CMD_USAGE, usage);
        }
    }

    protected String getUsage() {
        return null;
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

    protected void addSub(ICommand sub) {
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
