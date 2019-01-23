package org.soraworld.violet.command;

import org.soraworld.violet.api.ICommand;
import org.soraworld.violet.api.IManager;

import java.util.ArrayList;

public final class BaseSubCmds {

    private IManager manager;

    public BaseSubCmds(IManager manager) {
        this.manager = manager;
    }

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

    @Tab(path = "lang")
    public final TabExecutor tab_lang = (cmd, sender, args) -> new ArrayList<>();

    @Sub(perm = "admin")
    public final SubExecutor save = (cmd, sender, args) -> {
        if (args.empty()) {
            manager.sendKey(sender, manager.save() ? "configSaved" : "configSaveFailed");
        } else {
            ICommand sub = cmd.getSub(args.first());
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
            ICommand sub = cmd.getParent().getSub(args.first());
            if (sub != null) sub.sendUsage(sender);
            else manager.sendKey(sender, "noSuchSubCmd", args.first());
        } else cmd.sendUsage(sender);
    };

}
