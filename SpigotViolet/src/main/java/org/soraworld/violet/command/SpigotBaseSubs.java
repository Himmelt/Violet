package org.soraworld.violet.command;

import org.soraworld.violet.Violet;
import org.soraworld.violet.manager.FBManager;
import org.soraworld.violet.manager.SpigotManager;

public final class SpigotBaseSubs {

    @Sub(perm = "admin", tabs = {"zh_cn", "en_us"})
    public static final SpigotExecutor<SpigotManager> lang = (cmd, manager, sender, args) -> {
        if (args.notEmpty()) {
            if (manager.setLang(args.first())) {
                manager.asyncSave();
                manager.sendKey(sender, "setLang", manager.getLang());
            } else {
                manager.sendKey(sender, "setLangFailed", args.first());
            }
        } else manager.sendKey(sender, "getLang", manager.getLang());
    };

    @Sub(perm = "admin")
    public static final SpigotExecutor<SpigotManager> save = (cmd, manager, sender, args) -> {
        if (args.empty()) {
            manager.sendKey(sender, manager.save() ? "configSaved" : "configSaveFailed");
        } else {
            SpigotCommand sub = cmd.subs.get(args.first());
            if (sub != null) sub.execute(sender, args.next());
        }
    };

    @Sub(perm = "admin")
    public static final SpigotExecutor<SpigotManager> debug = (cmd, manager, sender, args) -> {
        manager.setDebug(!manager.isDebug());
        manager.sendKey(sender, manager.isDebug() ? "debugON" : "debugOFF");
    };

    @Sub(perm = "admin")
    public static final SpigotExecutor<SpigotManager> reload = (cmd, manager, sender, args) -> {
        manager.beforeLoad();
        manager.sendKey(sender, manager.load() ? "configLoaded" : "configLoadFailed");
        manager.afterLoad();
    };

    @Sub(perm = "admin")
    public static final SpigotExecutor<SpigotManager> rextract = (cmd, manager, sender, args) -> {
        manager.sendKey(sender, manager.reExtract() ? "reExtracted" : "reExtractFailed");
    };

    @Sub
    public static final SpigotExecutor<SpigotManager> help = (cmd, manager, sender, args) -> {
        if (args.notEmpty()) {
            SpigotCommand sub = cmd.getParent().getSub(args.first());
            if (sub != null) sub.sendUsage(sender);
            else manager.sendKey(sender, "noSuchSubCmd", args.first());
        } else cmd.sendUsage(sender);
    };

    @Sub(parent = Violet.PLUGIN_ID, perm = "admin")
    public static final SpigotExecutor<FBManager> plugins = (cmd, manager, sender, args) -> {
        manager.listPlugins(sender);
    };
}
