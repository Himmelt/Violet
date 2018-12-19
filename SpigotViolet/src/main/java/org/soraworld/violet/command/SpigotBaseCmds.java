package org.soraworld.violet.command;

import org.soraworld.violet.Violet;
import org.soraworld.violet.manager.FBManager;
import org.soraworld.violet.manager.SpigotManager;

import java.util.ArrayList;

public final class SpigotBaseCmds {

    @Sub(perm = "admin", tabs = {"zh_cn", "en_us"})
    public static final SpigotSub<SpigotManager> lang = (cmd, manager, sender, args) -> {
        if (args.notEmpty()) {
            if (manager.setLang(args.first())) {
                manager.asyncSave();
                manager.sendKey(sender, "setLang", manager.getLang());
            } else {
                manager.sendKey(sender, "setLangFailed", args.first());
            }
        } else manager.sendKey(sender, "getLang", manager.getLang());
    };

    @Tab(path = "lang")
    public static final SpigotTab<SpigotManager> tab_lang = (cmd, manager, sender, args) -> {
        return new ArrayList<>();
    };

    @Sub(perm = "admin")
    public static final SpigotSub<SpigotManager> save = (cmd, manager, sender, args) -> {
        if (args.empty()) {
            manager.sendKey(sender, manager.save() ? "configSaved" : "configSaveFailed");
        } else {
            SpigotCommand sub = cmd.subs.get(args.first());
            if (sub != null) sub.execute(sender, args.next());
        }
    };

    @Sub(perm = "admin")
    public static final SpigotSub<SpigotManager> debug = (cmd, manager, sender, args) -> {
        manager.setDebug(!manager.isDebug());
        manager.sendKey(sender, manager.isDebug() ? "debugON" : "debugOFF");
    };

    @Sub(perm = "admin")
    public static final SpigotSub<SpigotManager> reload = (cmd, manager, sender, args) -> {
        manager.beforeLoad();
        manager.sendKey(sender, manager.load() ? "configLoaded" : "configLoadFailed");
        manager.afterLoad();
    };

    @Sub(perm = "admin")
    public static final SpigotSub<SpigotManager> rextract = (cmd, manager, sender, args) -> {
        manager.sendKey(sender, manager.reExtract() ? "reExtracted" : "reExtractFailed");
    };

    @Sub
    public static final SpigotSub<SpigotManager> help = (cmd, manager, sender, args) -> {
        if (args.notEmpty()) {
            SpigotCommand sub = cmd.getParent().getSub(args.first());
            if (sub != null) sub.sendUsage(sender);
            else manager.sendKey(sender, "noSuchSubCmd", args.first());
        } else cmd.sendUsage(sender);
    };

    @Sub(parent = Violet.PLUGIN_ID, perm = "admin")
    public static final SpigotSub<FBManager> plugins = (cmd, manager, sender, args) -> {
        manager.listPlugins(sender);
    };
}
