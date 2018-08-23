package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.soraworld.violet.Violet;
import org.soraworld.violet.manager.SpigotManager;

import java.util.*;

import static org.soraworld.violet.Violet.*;

/**
 * Spigot 命令.
 */
public abstract class SpigotCommand {

    /**
     * 权限.
     */
    protected final String perm;
    /**
     * 只有玩家可以执行.
     */
    protected final boolean onlyPlayer;
    /**
     * 管理器.
     */
    protected final SpigotManager manager;

    /**
     * 命令别名.
     */
    protected final List<String> aliases = new ArrayList<>();
    /**
     * 子命令.
     */
    protected final HashMap<String, SpigotCommand> subs = new LinkedHashMap<>();

    /**
     * 实例化命令.
     * 至少填写一个别名，第一个别名将作为命令主名.
     *
     * @param perm       权限
     * @param onlyPlayer 是否仅玩家可执行
     * @param manager    管理器
     * @param aliases    别名
     */
    public SpigotCommand(String perm, boolean onlyPlayer, SpigotManager manager, String... aliases) {
        this.perm = perm;
        this.manager = manager;
        this.onlyPlayer = onlyPlayer;
        this.aliases.addAll(Arrays.asList(aliases));
    }

    /**
     * 非 仅玩家执行.
     * notOnlyPlayer
     *
     * @return 是否 非仅玩家执行
     */
    public boolean nop() {
        return !onlyPlayer;
    }

    /**
     * 用法.
     *
     * @return 用法
     */
    public String getUsage() {
        return "";
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
        for (String alias : sub.aliases) {
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
                if (sub.canRun(sender)) {
                    args.next();
                    if (sender instanceof org.bukkit.entity.Player) sub.execute((org.bukkit.entity.Player) sender, args);
                    else if (!sub.onlyPlayer) sub.execute(sender, args);
                    else manager.sendKey(sender, Violet.KEY_ONLY_PLAYER);
                } else manager.sendKey(sender, Violet.KEY_NO_CMD_PERM);
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
     * 命令发送者是否能执行此命令.
     *
     * @param sender 命令发送者
     * @return 是否能执行此命令
     */
    public boolean canRun(CommandSender sender) {
        return perm == null || sender.hasPermission(perm);
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

    /**
     * Violet 命令.
     */
    public static class CommandViolet extends SpigotCommand {
        /**
         * 实例化.
         *
         * @param perm       权限
         * @param onlyPlayer 是否仅玩家执行
         * @param manager    管理器
         * @param aliases    别名
         */
        public CommandViolet(String perm, boolean onlyPlayer, SpigotManager manager, String... aliases) {
            super(perm, onlyPlayer, manager, aliases);
            addSub(new SpigotCommand(manager.defAdminPerm(), false, manager, "lang") {
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
