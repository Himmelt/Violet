package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.soraworld.violet.Violet;
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
    private final List<String> aliases;
    private final TreeMap<String, IICommand> subs = new TreeMap<>();

    public IICommand(String name, String perm, IIConfig config, String... aliases) {
        this.name = name;
        this.perm = perm;
        this.config = config;
        this.aliases = ListUtil.arrayList(aliases);
    }

    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (perm != null && !sender.hasPermission(perm)) {
            config.iiChat.send(sender, Violet.translate(config.getLang(), Violets.KEY_NO_CMD_PERM, perm));
            return false;
        }
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


}
