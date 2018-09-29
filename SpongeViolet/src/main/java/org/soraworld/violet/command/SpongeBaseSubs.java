package org.soraworld.violet.command;

import org.soraworld.violet.manager.SpongeManager;
import org.spongepowered.api.command.CommandSource;

/**
 * Sponge 基础子命令 注解集合.
 */
public final class SpongeBaseSubs {
    /**
     * 查看/设置 语言.
     *
     * @param self   封装命令
     * @param sender 命令执行者
     * @param args   参数
     */
    @Sub(perm = "admin", tabs = {"zh_cn", "en_us"})
    public static void lang(SpongeCommand self, CommandSource sender, Args args) {
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
    public static void save(SpongeCommand self, CommandSource sender, Args args) {
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
    public static void debug(SpongeCommand self, CommandSource sender, Args args) {
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
    public static void reload(SpongeCommand self, CommandSource sender, Args args) {
        self.manager.beforeLoad();
        self.manager.sendKey(sender, self.manager.load() ? "configLoaded" : "configLoadFailed");
        self.manager.afterLoad();
    }

    /**
     * 重新从jar释放语言文件.
     *
     * @param self   封装命令
     * @param sender 命令执行者
     * @param args   参数
     */
    @Sub(perm = "admin")
    public static void rextract(SpongeCommand self, CommandSource sender, Args args) {
        self.manager.sendKey(sender, self.manager.reExtract() ? "reExtracted" : "reExtractFailed");
    }

    /**
     * 查看帮助.
     *
     * @param self   封装命令
     * @param sender 命令执行者
     * @param args   参数
     */
    @Sub
    public static void help(SpongeCommand self, CommandSource sender, Args args) {
        if (args.notEmpty()) {
            SpongeCommand sub = self.parent.getSub(args.first());
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
    public static void plugins(SpongeCommand self, CommandSource sender, Args args) {
        if (self.manager instanceof SpongeManager.Manager) {
            ((SpongeManager.Manager) self.manager).listPlugins(sender);
        }
    }
}
