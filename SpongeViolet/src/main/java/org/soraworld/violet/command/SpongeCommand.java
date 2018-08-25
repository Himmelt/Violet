package org.soraworld.violet.command;

import org.soraworld.violet.Violet;
import org.soraworld.violet.manager.SpongeManager;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

import static org.soraworld.violet.Violet.*;

/**
 * Sponge 命令.
 */
public abstract class SpongeCommand {

    /**
     * 权限.
     */
    protected final String perm;
    /**
     * 是否仅玩家执行.
     */
    protected final boolean onlyPlayer;
    /**
     * 管理器.
     */
    protected final SpongeManager manager;

    /**
     * 别名列表.
     */
    protected final List<String> aliases = new ArrayList<>();
    /**
     * 子命令.
     */
    protected final HashMap<String, SpongeCommand> subs = new LinkedHashMap<>();

    /**
     * 实例化 Sponge 命令.
     *
     * @param perm       权限
     * @param onlyPlayer 是否仅玩家执行
     * @param manager    管理器
     * @param aliases    别名
     */
    public SpongeCommand(String perm, boolean onlyPlayer, SpongeManager manager, String... aliases) {
        this.perm = perm;
        this.manager = manager;
        this.onlyPlayer = onlyPlayer;
        this.aliases.addAll(Arrays.asList(aliases));
    }

    /**
     * 执行命令.
     *
     * @param sender 命令发送者
     * @param args   参数
     */
    public void execute(CommandSource sender, CommandArgs args) {
        if (args.notEmpty()) {
            SpongeCommand sub = subs.get(args.first());
            if (sub != null) {
                if (sub.canRun(sender)) {
                    args.next();
                    if (sender instanceof Player) sub.execute((Player) sender, args);
                    else if (!sub.onlyPlayer) sub.execute(sender, args);
                    else manager.sendKey(sender, Violet.KEY_ONLY_PLAYER);
                } else manager.sendKey(sender, Violet.KEY_NO_CMD_PERM, sub.perm);
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
        execute((CommandSource) player, args);
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
    public void addSub(SpongeCommand sub) {
        for (String alias : sub.aliases) {
            subs.putIfAbsent(alias, sub);
        }
    }

    /**
     * 发送使用方法.
     *
     * @param sender 信息接收者
     */
    protected void sendUsage(CommandSource sender) {
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
    protected boolean canRun(CommandSource sender) {
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
    public static class CommandViolet extends SpongeCommand {
        /**
         * Instantiates a new Command violet.
         *
         * @param perm       the perm
         * @param onlyPlayer the only player
         * @param manager    the manager
         * @param aliases    the aliases
         */
        public CommandViolet(String perm, boolean onlyPlayer, SpongeManager manager, String... aliases) {
            super(perm, onlyPlayer, manager, aliases);
            addSub(new SpongeCommand(manager.defAdminPerm(), false, manager, "lang") {
                public void execute(CommandSource sender, CommandArgs args) {
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
            addSub(new SpongeCommand(manager.defAdminPerm(), false, manager, "save") {
                public void execute(CommandSource sender, CommandArgs args) {
                    manager.sendKey(sender, manager.save() ? KEY_CFG_SAVE : KEY_CFG_SAVE_FAIL);
                }
            });
            addSub(new SpongeCommand(manager.defAdminPerm(), false, manager, "debug") {
                public void execute(CommandSource sender, CommandArgs args) {
                    manager.setDebug(!manager.isDebug());
                    manager.sendKey(sender, manager.isDebug() ? KEY_DEBUG_ON : KEY_DEBUG_OFF);
                }
            });
            addSub(new SpongeCommand(manager.defAdminPerm(), false, manager, "reload") {
                public void execute(CommandSource sender, CommandArgs args) {
                    manager.sendKey(sender, manager.load() ? KEY_CFG_LOAD : KEY_CFG_LOAD_FAIL);
                }
            });
        }

        public String getUsage() {
            return "/violet lang|debug|save|reload";
        }
    }
}
