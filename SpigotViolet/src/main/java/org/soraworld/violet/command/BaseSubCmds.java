package org.soraworld.violet.command;

import org.soraworld.violet.Violet;
import org.soraworld.violet.inject.Command;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.manager.FManager;
import org.soraworld.violet.manager.VManager;
import org.soraworld.violet.util.ListUtils;

import java.util.LinkedHashSet;

@Command(name = Violet.PLUGIN_ID, usage = "/violet lang|save|debug|reload|rextract|help|plugins ")
public final class BaseSubCmds {

    @Inject
    private VManager manager;
    @Inject
    private FManager fManager;

    @Sub(perm = "admin", tabs = {"zh_cn", "en_us"}, usage = "usage.lang")
    public final SubExecutor lang = (cmd, sender, args) -> {
        if (args.notEmpty()) {
            String oldLang = manager.getLang();
            if (manager.setLang(args.first())) {
                manager.sendKey(sender, "setLang", manager.getLang());
                if (!manager.getLang().equalsIgnoreCase(oldLang)) manager.asyncSave(null);
            } else {
                manager.sendKey(sender, "setLangFailed", args.first());
            }
        } else manager.sendKey(sender, "getLang", manager.getLang());
    };

    @Sub(perm = "admin", usage = "usage.save")
    public final SubExecutor save = (cmd, sender, args) -> {
        if (args.notEmpty()) {
            VCommand sub = cmd.getSub(args.first());
            if (sub != null) sub.execute(sender, args.next());
        } else manager.asyncSave(sender);
    };

    @Sub(perm = "admin", usage = "usage.debug")
    public final SubExecutor debug = (cmd, sender, args) -> {
        manager.setDebug(!manager.isDebug());
        manager.sendKey(sender, manager.isDebug() ? "debugON" : "debugOFF");
    };

    @Sub(perm = "admin", usage = "usage.reload", tabs = {"lang"})
    public final SubExecutor reload = (cmd, sender, args) -> {
        if (args.first().equalsIgnoreCase("lang")) {
            manager.setLang(manager.getLang());
            manager.sendKey(sender, "reloadLang");
        } else {
            manager.beforeLoad();
            manager.sendKey(sender, manager.load() ? "configLoaded" : "configLoadFailed");
            manager.afterLoad();
        }
    };

    @Sub(perm = "admin", usage = "usage.rextract")
    public final SubExecutor rextract = (cmd, sender, args) -> manager.sendKey(sender, manager.reExtract() ? "reExtracted" : "reExtractFailed");

    @Sub(perm = "admin", usage = "usage.backup")
    public final SubExecutor backup = (cmd, sender, args) -> manager.asyncBackUp(sender);

    @Sub
    public final SubExecutor help = (cmd, sender, args) -> {
        if (args.notEmpty()) {
            VCommand sub = cmd.parent.getSub(args.first());
            if (sub != null) sub.sendUsage(sender);
            else manager.sendKey(sender, "noSuchSubCmd", args.first());
        } else {
            LinkedHashSet<VCommand> subs = new LinkedHashSet<>(cmd.parent.subs.values());
            subs.remove(cmd);
            for (VCommand sub : subs) sub.sendUsage(sender);
        }
    };

    @Tab(path = "help")
    public final TabExecutor tab_help = (cmd, sender, args) -> ListUtils.getMatchList(args.first(), cmd.parent.subs.keySet());

    @Sub(parent = Violet.PLUGIN_ID, perm = "admin", usage = "usage.plugins")
    public final SubExecutor plugins = (cmd, sender, args) -> {
        fManager.listPlugins(sender);
    };
}
