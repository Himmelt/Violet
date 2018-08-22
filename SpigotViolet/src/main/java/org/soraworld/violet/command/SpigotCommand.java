package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.soraworld.violet.Violet;
import org.soraworld.violet.manager.SpigotManager;

import java.util.*;

import static org.soraworld.violet.Violet.*;

public abstract class SpigotCommand {

    protected final String perm;
    protected final boolean onlyPlayer;
    protected final SpigotManager manager;

    protected final List<String> aliases = new ArrayList<>();
    protected final HashMap<String, SpigotCommand> subs = new LinkedHashMap<>();

    public SpigotCommand(String perm, boolean onlyPlayer, SpigotManager manager, String... aliases) {
        this.perm = perm;
        this.manager = manager;
        this.onlyPlayer = onlyPlayer;
        this.aliases.addAll(Arrays.asList(aliases));
    }

    public boolean nop() {
        return !onlyPlayer;
    }

    public String getUsage() {
        return "";
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

    public void addSub(SpigotCommand sub) {
        for (String alias : sub.aliases) {
            subs.putIfAbsent(alias, sub);
        }
    }

    public void execute(CommandSender sender, CommandArgs args) {
        if (args.notEmpty()) {
            SpigotCommand sub = subs.get(args.first());
            if (sub != null) {
                if (sub.canRun(sender)) {
                    args.next();
                    if (sender instanceof org.bukkit.entity.Player) sub.execute((org.bukkit.entity.Player) sender, args);
                    else if (!sub.onlyPlayer) sub.execute(sender, args);
                    else manager.sendKey(sender, Violet.KEY_ONLY_PLAYER);
                } else manager.sendKey(sender, Violet.KEY_NO_CMD_PERM);
            } else sendUsage(sender);
        } else sendUsage(sender);
    }

    public void execute(Player player, CommandArgs args) {
        execute((CommandSender) player, args);
    }

    protected void sendUsage(CommandSender sender) {
        String usage = getUsage();
        if (usage != null && !usage.isEmpty()) {
            manager.sendKey(sender, KEY_CMD_USAGE, usage);
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

    public static class CommandViolet extends SpigotCommand {
        public CommandViolet(String perm, boolean onlyPlayer, SpigotManager manager, String... aliases) {
            super(perm, onlyPlayer, manager, aliases);
            addSub(new SpigotCommand(manager.defAdminPerm(), false, manager, "lang") {
                public void execute(CommandSender sender, CommandArgs args) {
                    if (args.notEmpty()) {
                        if (manager.setLang(args.first())) {
                            manager.save();
                            manager.sendKey(sender, KEY_SET_LANG, manager.getLang());
                        } else {
                            manager.sendKey(sender, KEY_SET_LANG_FAILED, args.first());
                        }
                    } else manager.sendKey(sender, KEY_GET_LANG, manager.getLang());
                }
            });
            addSub(new SpigotCommand(manager.defAdminPerm(), false, manager, "save") {
                public void execute(CommandSender sender, CommandArgs args) {
                    manager.sendKey(sender, manager.save() ? KEY_CFG_SAVE : KEY_CFG_SAVE_FAIL);
                }
            });
            addSub(new SpigotCommand(manager.defAdminPerm(), false, manager, "debug") {
                public void execute(CommandSender sender, CommandArgs args) {
                    manager.setDebug(!manager.isDebug());
                    manager.sendKey(sender, manager.isDebug() ? KEY_DEBUG_ON : KEY_DEBUG_OFF);
                }
            });
            addSub(new SpigotCommand(manager.defAdminPerm(), false, manager, "reload") {
                public void execute(CommandSender sender, CommandArgs args) {
                    manager.sendKey(sender, manager.load() ? KEY_CFG_LOAD : KEY_CFG_LOAD_FAIL);
                }
            });
        }

        public String getUsage() {
            return "/violet lang|debug|save|reload";
        }
    }
}
