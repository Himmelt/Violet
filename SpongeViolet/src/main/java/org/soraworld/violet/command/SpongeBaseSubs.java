package org.soraworld.violet.command;

import org.soraworld.violet.Violet;
import org.soraworld.violet.manager.FSManager;
import org.soraworld.violet.manager.SpongeManager;

public final class SpongeBaseSubs {

    @Sub(perm = "admin", tabs = {"zh_cn", "en_us"})
    public static final SpongeExecutor<SpongeManager> lang = (cmd, manager, sender, args) -> {
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
    public static final SpongeExecutor<SpongeManager> save = (cmd, manager, sender, args) -> {
        if (args.empty()) {
            manager.sendKey(sender, manager.save() ? "configSaved" : "configSaveFailed");
        } else {
            SpongeCommand sub = cmd.subs.get(args.first());
            if (sub != null) sub.execute(sender, args.next());
        }
    };

    @Sub(perm = "admin")
    public static final SpongeExecutor<SpongeManager> debug = (cmd, manager, sender, args) -> {
        manager.setDebug(!manager.isDebug());
        manager.sendKey(sender, manager.isDebug() ? "debugON" : "debugOFF");
    };

    @Sub(perm = "admin")
    public static final SpongeExecutor<SpongeManager> reload = (cmd, manager, sender, args) -> {
        manager.beforeLoad();
        manager.sendKey(sender, manager.load() ? "configLoaded" : "configLoadFailed");
        manager.afterLoad();
    };

    @Sub(perm = "admin")
    public static final SpongeExecutor<SpongeManager> rextract = (cmd, manager, sender, args) -> {
        manager.sendKey(sender, manager.reExtract() ? "reExtracted" : "reExtractFailed");
    };

    @Sub
    public static final SpongeExecutor<SpongeManager> help = (cmd, manager, sender, args) -> {
        if (args.notEmpty()) {
            SpongeCommand sub = cmd.parent.getSub(args.first());
            if (sub != null) sub.sendUsage(sender);
            else manager.sendKey(sender, "noSuchSubCmd", args.first());
        } else cmd.sendUsage(sender);
    };

    @Sub(parent = Violet.PLUGIN_ID, perm = "admin")
    public static final SpongeExecutor<FSManager> plugins = (cmd, manager, sender, args) -> {
        manager.listPlugins(sender);
    };
}
