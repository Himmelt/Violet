package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.soraworld.violet.manager.SpigotManager;

/**
 * Spigot 基础子命令 注解集合.
 */
public final class SpigotBaseSubs {
    /**
     * 查看/设置 语言.
     *
     * @param self   封装命令
     * @param sender 命令执行者
     * @param args   参数
     */
    @Sub(perm = "admin", tabs = {"zh_cn", "en_us"})
    public static void lang(SpigotCommand self, CommandSender sender, Paths args) {
        if (args.notEmpty()) {
            if (self.manager.setLang(args.first())) {
                self.manager.asyncSave();
                self.manager.sendKey(sender, "setLang", self.manager.getLang());
            } else {
                self.manager.sendKey(sender, "setLangFailed", args.first());
            }
        } else self.manager.sendKey(sender, "getLang", self.manager.getLang());
    }

    /**
     * 保存配置到文件.
     *
     * @param self   封装命令
     * @param sender 命令执行者
     * @param args   参数
     */
    @Sub(perm = "admin")
    public static void save(SpigotCommand self, CommandSender sender, Paths args) {
        self.manager.sendKey(sender, self.manager.save() ? "configSaved" : "configSaveFailed");
    }

    /**
     * 开启/关闭 调试模式.
     *
     * @param self   封装命令
     * @param sender 命令执行者
     * @param args   参数
     */
    @Sub(perm = "admin")
    public static void debug(SpigotCommand self, CommandSender sender, Paths args) {
        self.manager.setDebug(!self.manager.isDebug());
        self.manager.sendKey(sender, self.manager.isDebug() ? "debugON" : "debugOFF");
    }

    /**
     * 从文件重载配置.
     *
     * @param self   封装命令
     * @param sender 命令执行者
     * @param args   参数
     */
    @Sub(perm = "admin")
    public static void reload(SpigotCommand self, CommandSender sender, Paths args) {
        self.manager.sendKey(sender, self.manager.load() ? "configLoaded" : "configLoadFailed");
    }

    /**
     * 查看帮助.
     *
     * @param self   封装命令
     * @param sender 命令执行者
     * @param args   参数
     */
    @Sub
    public static void help(SpigotCommand self, CommandSender sender, Paths args) {
        if (args.notEmpty()) {
            SpigotCommand sub = self.getParent().getSub(args.first());
            if (sub != null) sub.sendUsage(sender);
            else self.manager.sendKey(sender, "noSuchSubCmd", args.first());
        } else self.sendUsage(sender);
    }

    /**
     * 列出服务器运行的 Violet 插件.
     *
     * @param self   封装命令
     * @param sender 命令执行者
     * @param args   参数
     */
    @Sub(perm = "admin")
    public static void plugins(SpigotCommand self, CommandSender sender, Paths args) {
        if (self.manager instanceof SpigotManager.Manager) {
            ((SpigotManager.Manager) self.manager).listPlugins(sender);
        }
    }
}
