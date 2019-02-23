package org.soraworld.violet.command;

import org.soraworld.violet.Violet;
import org.soraworld.violet.inject.Command;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.manager.FManager;
import org.soraworld.violet.manager.VManager;

@Command(name = Violet.PLUGIN_ID, usage = "/violet lang|save|debug|reload|rextract|help|plugins ")
public final class BaseSubCmds {

    @Inject
    private VManager manager;
    @Inject
    private FManager vManager;

    @Sub(perm = "admin", tabs = {"zh_cn", "en_us"})
    public final SubExecutor lang = (cmd, sender, args) -> {
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
    public final SubExecutor save = (cmd, sender, args) -> {
        if (args.empty()) {
            manager.sendKey(sender, manager.save() ? "configSaved" : "configSaveFailed");
        } else {
            VCommand sub = cmd.getSub(args.first());
            if (sub != null) sub.execute(sender, args.next());
        }
    };

    @Sub(perm = "admin")
    public final SubExecutor debug = (cmd, sender, args) -> {
        manager.setDebug(!manager.isDebug());
        manager.sendKey(sender, manager.isDebug() ? "debugON" : "debugOFF");
    };

    @Sub(perm = "admin")
    public final SubExecutor reload = (cmd, sender, args) -> {
        manager.beforeLoad();
        manager.sendKey(sender, manager.load() ? "configLoaded" : "configLoadFailed");
        manager.afterLoad();
    };

    @Sub(perm = "admin")
    public final SubExecutor rextract = (cmd, sender, args) -> {
        manager.sendKey(sender, manager.reExtract() ? "reExtracted" : "reExtractFailed");
    };

    @Sub
    public final SubExecutor help = (cmd, sender, args) -> {
        if (args.notEmpty()) {
            VCommand sub = cmd.parent.getSub(args.first());
            if (sub != null) sub.sendUsage(sender);
            else manager.sendKey(sender, "noSuchSubCmd", args.first());
        } else cmd.parent.sendUsage(sender);
    };

    @Sub(parent = Violet.PLUGIN_ID, perm = "admin")
    public final SubExecutor plugins = (cmd, sender, args) -> {
        vManager.listPlugins(sender);
    };
}
