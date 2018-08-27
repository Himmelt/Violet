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
     * @param manager 管理器
     * @param sender  命令执行者
     * @param args    参数
     */
    @Sub(perm = "admin", tabs = {"zh_cn", "en_us"})
    public static void lang(SpigotManager manager, CommandSender sender, Paths args) {
        if (args.notEmpty()) {
            if (manager.setLang(args.first())) {
                manager.asyncSave();
                manager.sendKey(sender, "setLang", manager.getLang());
            } else {
                manager.sendKey(sender, "setLangFailed", args.first());
            }
        } else manager.sendKey(sender, "getLang", manager.getLang());
    }

    /**
     * 保存配置到文件.
     *
     * @param manager 管理器
     * @param sender  命令执行者
     * @param args    参数
     */
    @Sub(perm = "admin")
    public static void save(SpigotManager manager, CommandSender sender, Paths args) {
        manager.sendKey(sender, manager.save() ? "configSaved" : "configSaveFailed");
    }

    /**
     * 开启/关闭 调试模式.
     *
     * @param manager 管理器
     * @param sender  命令执行者
     * @param args    参数
     */
    @Sub(perm = "admin")
    public static void debug(SpigotManager manager, CommandSender sender, Paths args) {
        manager.setDebug(!manager.isDebug());
        manager.sendKey(sender, manager.isDebug() ? "debugON" : "debugOFF");
    }

    /**
     * 从文件重载配置.
     *
     * @param manager 管理器
     * @param sender  命令执行者
     * @param args    参数
     */
    @Sub(perm = "admin")
    public static void reload(SpigotManager manager, CommandSender sender, Paths args) {
        manager.sendKey(sender, manager.load() ? "configLoaded" : "configLoadFailed");
    }

    /**
     * 列出服务器运行的 Violet 插件.
     *
     * @param manager 管理器
     * @param sender  命令执行者
     * @param args    参数
     */
    @Sub(perm = "admin")
    public static void plugins(SpigotManager manager, CommandSender sender, Paths args) {
        if (manager instanceof SpigotManager.Manager) {
            ((SpigotManager.Manager) manager).listPlugins(sender);
        }
    }
}
