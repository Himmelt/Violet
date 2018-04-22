package org.soraworld.violet.command;

import org.soraworld.violet.api.IPlayer;
import org.soraworld.violet.api.VioletSender;
import org.soraworld.violet.config.VioletManager;
import org.soraworld.violet.constant.Violets;

import java.util.*;

public abstract class IICommand {

    private final String perm;
    private final boolean onlyPlayer;
    private final VioletManager manager;

    private final List<String> aliases = new ArrayList<>();
    private final TreeMap<String, IICommand> subs = new TreeMap<>();

    public IICommand(String perm, boolean onlyPlayer, VioletManager manager, String... aliases) {
        this.perm = perm;
        this.onlyPlayer = onlyPlayer;
        this.manager = manager;
        this.aliases.addAll(Arrays.asList(aliases));
    }

    public boolean execute(VioletSender sender, ArrayList<String> args) {
        if (args.isEmpty()) return false;
        IICommand sub = subs.get(args.remove(0));
        if (sub == null) return false;
        if (sub.canRun(sender)) {
            if (sender instanceof IPlayer) {
                sub.execute((IPlayer) sender, args);
            } else if (onlyPlayer) {
                manager.vSendKey(sender, Violets.KEY_ONLY_PLAYER);
            } else {
                sub.execute(sender, args);
            }
        } else manager.vSendKey(sender, Violets.KEY_NO_CMD_PERM, sub.perm);
        return true;
    }

    public boolean execute(IPlayer player, ArrayList<String> args) {
        return execute((VioletSender) player, args);
    }

    protected void addSub(IICommand sub) {
        for (String alias : sub.aliases) {
            subs.putIfAbsent(alias, sub);
        }
    }

    private boolean canRun(VioletSender sender) {
        return perm == null || sender.hasPermission(perm);
    }

    public static List<String> getMatchList(String text, Collection<String> possibles) {
        ArrayList<String> list = new ArrayList<>();
        if (text.isEmpty()) list.addAll(possibles);
        else for (String s : possibles) if (s.startsWith(text)) list.add(s);
        return list;
    }

}
