package org.soraworld.violet.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.soraworld.violet.Violet;
import org.soraworld.violet.manager.SpigotManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static org.soraworld.violet.Violet.*;

/**
 * Spigot 命令.
 */
public abstract class SpigotCommand extends Command {

    /**
     * 只有玩家可以执行.
     */
    protected final boolean onlyPlayer;
    /**
     * 管理器.
     */
    protected final SpigotManager manager;
    /**
     * 子命令.
     */
    protected final HashMap<String, SpigotCommand> subs = new LinkedHashMap<>();

    /**
     * 实例化 Spigot 命令.
     *
     * @param name       命令主名
     * @param perm       权限
     * @param onlyPlayer 是否仅玩家可执行
     * @param manager    管理器
     * @param aliases    别名
     */
    public SpigotCommand(@Nonnull String name, @Nullable String perm, boolean onlyPlayer, @Nonnull SpigotManager manager, String... aliases) {
        super(name);
        setPermission(perm);
        this.manager = manager;
        this.onlyPlayer = onlyPlayer;
        ArrayList<String> list = new ArrayList<>(Arrays.asList(aliases));
        list.removeIf(s -> s == null || s.isEmpty() || s.contains(" ") || s.contains(":"));
        setAliases(list);
    }

    /**
     * 获取tab补全列表.
     *
     * @param args 参数
     * @return 补全列表
     */
    public List<String> tabCompletions(CommandArgs args) {
        String first = args.first();
        if (args.size() == 1) return getMatchList(first, subs.keySet());
        if (subs.containsKey(first)) {
            args.next();
            return subs.get(first).tabCompletions(args);
        }
        return new ArrayList<>();
    }

    /**
     * 添加子命令.
     *
     * @param sub 子命令
     */
    public void addSub(SpigotCommand sub) {
        subs.put(sub.getName(), sub);
        for (String alias : sub.getAliases()) {
            subs.putIfAbsent(alias, sub);
        }
    }

    /**
     * 执行命令.
     *
     * @param sender 命令发送者
     * @param args   参数
     */
    public void execute(CommandSender sender, CommandArgs args) {
        if (args.notEmpty()) {
            SpigotCommand sub = subs.get(args.first());
            if (sub != null) {
                if (sub.testPermission(sender)) {
                    args.next();
                    if (sender instanceof Player) sub.execute((Player) sender, args);
                    else if (!sub.onlyPlayer) sub.execute(sender, args);
                    else manager.sendKey(sender, Violet.KEY_ONLY_PLAYER);
                } else manager.sendKey(sender, Violet.KEY_NO_CMD_PERM, sub.getPermission());
            } else sendUsage(sender);
        } else sendUsage(sender);
    }

    /**
     * 执行玩家命令.
     *
     * @param player 玩家
     * @param args   参数
     */
    public void execute(Player player, CommandArgs args) {
        execute((CommandSender) player, args);
    }

    /**
     * 发送使用方法.
     *
     * @param sender 信息接收者
     */
    protected void sendUsage(CommandSender sender) {
        String usage = getUsage();
        if (usage != null && !usage.isEmpty()) {
            manager.sendKey(sender, KEY_CMD_USAGE, usage);
        }
    }

    /**
     * 获取字符串匹配列表.
     *
     * @param text      待匹配文本
     * @param possibles 待筛选列表
     * @return 匹配后列表
     */
    public static List<String> getMatchList(String text, Collection<String> possibles) {
        ArrayList<String> list = new ArrayList<>();
        if (text.isEmpty()) list.addAll(possibles);
        else for (String s : possibles) if (s.startsWith(text)) list.add(s);
        return list;
    }

    public final boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (testPermission(sender)) {
            if (sender instanceof Player) execute(((Player) sender), new CommandArgs(args));
            else if (!onlyPlayer) execute(sender, new CommandArgs(args));
            else manager.sendKey(sender, Violet.KEY_ONLY_PLAYER);
        } else manager.sendKey(sender, Violet.KEY_NO_CMD_PERM, getPermission());
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return tabCompletions(new CommandArgs(args));
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) {
        return tabCompletions(new CommandArgs(args));
    }

    /**
     * 命令发送者是否能执行此命令.
     *
     * @param target 命令发送者
     * @return 是否能执行此命令
     */
    public final boolean testPermission(CommandSender target) {
        return getPermission() == null || target.hasPermission(getPermission());
    }

    /**
     * 命令发送者是否能执行此命令.
     *
     * @param target 命令发送者
     * @return 是否能执行此命令
     */
    public final boolean testPermissionSilent(CommandSender target) {
        return getPermission() == null || target.hasPermission(getPermission());
    }

    /**
     * Violet 命令.
     */
    public static class CommandViolet extends SpigotCommand {
        /**
         * 实例化 violet 命令.
         *
         * @param name       命令主名
         * @param perm       权限
         * @param onlyPlayer 是否仅玩家执行
         * @param manager    管理器
         * @param aliases    别名
         */
        public CommandViolet(@Nonnull String name, @Nullable String perm, boolean onlyPlayer, @Nonnull SpigotManager manager, String... aliases) {
            super(name, perm, onlyPlayer, manager, aliases);
            addSub(new SpigotCommand("lang", manager.defAdminPerm(), false, manager) {
                public void execute(CommandSender sender, CommandArgs args) {
                    if (args.notEmpty()) {
                        if (manager.setLang(args.first())) {
                            manager.asyncSave();
                            manager.sendKey(sender, KEY_SET_LANG, manager.getLang());
                        } else {
                            manager.sendKey(sender, KEY_SET_LANG_FAILED, args.first());
                        }
                    } else manager.sendKey(sender, KEY_GET_LANG, manager.getLang());
                }
            });
            addSub(new SpigotCommand("save", manager.defAdminPerm(), false, manager) {
                public void execute(CommandSender sender, CommandArgs args) {
                    manager.sendKey(sender, manager.save() ? KEY_CFG_SAVE : KEY_CFG_SAVE_FAIL);
                }
            });
            addSub(new SpigotCommand("debug", manager.defAdminPerm(), false, manager) {
                public void execute(CommandSender sender, CommandArgs args) {
                    manager.setDebug(!manager.isDebug());
                    manager.sendKey(sender, manager.isDebug() ? KEY_DEBUG_ON : KEY_DEBUG_OFF);
                }
            });
            addSub(new SpigotCommand("reload", manager.defAdminPerm(), false, manager) {
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
