package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.soraworld.violet.config.IIConfig;
import org.soraworld.violet.constant.Violets;
import org.soraworld.violet.util.ListUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public abstract class IICommand {

    private final String name;
    private final String perm;
    private final IIConfig config;
    private final boolean onlyPlayer;
    private final List<String> aliases;
    private final TreeMap<String, IICommand> subs = new TreeMap<>();

    public IICommand(String name, IIConfig config, String... aliases) {
        this(name, null, config, false, aliases);
    }

    public IICommand(String name, String perm, IIConfig config, String... aliases) {
        this(name, perm, config, false, aliases);
    }

    public IICommand(String name, String perm, IIConfig config, boolean onlyPlayer, String... aliases) {
        this.name = name;
        this.perm = perm;
        this.config = config;
        this.onlyPlayer = onlyPlayer;
        this.aliases = ListUtil.arrayList(aliases);
    }

    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (args.size() >= 1) {
            IICommand sub = subs.get(args.remove(0));
            if (sub != null) {
                if (sub.onlyPlayer) {
                    if (sender instanceof Player) {
                        if (sub.canRun(sender)) return sub.execute((Player) sender, args);
                        config.sendV(sender, Violets.KEY_NO_CMD_PERM, sub.perm);
                        return true;
                    }
                    config.sendV(sender, Violets.KEY_ONLY_PLAYER);
                    return true;
                }
                if (sub.canRun(sender)) return sub.execute(sender, args);
                config.sendV(sender, Violets.KEY_NO_CMD_PERM, sub.perm);
                return true;
            }
        }
        if (!getUsage().isEmpty()) sender.sendMessage(getUsage());
        return true;
    }

    public boolean execute(Player player, ArrayList<String> args) {
        return execute((CommandSender) player, args);
    }

    @Nonnull
    protected String getUsage() {
        return "";
    }

    protected final void addSub(IICommand sub) {
        this.subs.put(sub.name, sub);
        for (String alias : sub.aliases) {
            IICommand command = this.subs.get(alias);
            if (command == null || !command.name.equals(alias)) {
                this.subs.put(alias, sub);
            }
        }
    }

    public List<String> getTabCompletions(ArrayList<String> args) {
        if (args.size() == 1) {
            return ListUtil.getMatchList(args.get(0), subs.keySet());
        } else if (args.size() >= 2) {
            IICommand sub = subs.get(args.remove(0));
            if (sub != null) return sub.getTabCompletions(args);
            else return new ArrayList<>();
        } else {
            return new ArrayList<>(subs.keySet());
        }
    }

    private boolean canRun(CommandSender sender) {
        return perm == null || sender.hasPermission(perm);
    }

}
