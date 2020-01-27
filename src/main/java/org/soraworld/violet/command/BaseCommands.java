package org.soraworld.violet.command;

import org.soraworld.violet.Violet;
import org.soraworld.violet.api.ICommandSender;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.core.ManagerCore;
import org.soraworld.violet.inject.Command;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.util.ListUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * @author Himmelt
 */
@Command(name = Violet.PLUGIN_ID, usage = "/violet lang|save|debug|reload|rextract|help|plugins ")
public final class BaseCommands {

    @Inject
    private ManagerCore core;
    @Inject
    private IManager manager;

    @Sub(perm = "admin", tabs = {"zh_cn", "en_us"}, usage = "usage.lang")
    public final SubExecutor<ICommandSender> lang = (cmd, sender, args) -> {
        if (args.notEmpty()) {
            String oldLang = manager.getLang();
            if (manager.setLang(args.first())) {
                manager.sendKey(sender, "setLang", manager.getLang());
                if (!manager.getLang().equalsIgnoreCase(oldLang)) {
                    manager.asyncSave(null);
                }
            } else {
                manager.sendKey(sender, "setLangFailed", args.first());
            }
        } else {
            manager.sendKey(sender, "getLang", manager.getLang());
        }
    };

    @Sub(perm = "admin", usage = "usage.save")
    public final SubExecutor save = (cmd, sender, args) -> {
        if (args.notEmpty()) {
            CommandCore sub = cmd.getSub(args.first());
            if (sub != null) {
                sub.execute(sender, args.next());
            }
        } else {
            manager.asyncSave(sender);
        }
    };

    @Sub(perm = "admin", usage = "usage.debug")
    public final SubExecutor<ICommandSender> debug = (cmd, sender, args) -> {
        manager.setDebug(!manager.isDebug());
        manager.sendKey(sender, manager.isDebug() ? "debugON" : "debugOFF");
    };

    @Sub(perm = "admin", usage = "usage.reload", tabs = {"lang"})
    public final SubExecutor<ICommandSender> reload = (cmd, sender, args) -> {
        if ("lang".equalsIgnoreCase(args.first())) {
            manager.setLang(manager.getLang());
            manager.sendKey(sender, "reloadLang");
        } else {
            manager.sendKey(sender, manager.load() ? "configLoaded" : "configLoadFailed");
        }
    };

    @Sub(perm = "admin", usage = "usage.rextract")
    public final SubExecutor<ICommandSender> rextract = (cmd, sender, args) -> manager.sendKey(sender, manager.reExtract() ? "reExtracted" : "reExtractFailed");

    @Sub(perm = "admin", usage = "usage.backup")
    public final SubExecutor<ICommandSender> backup = (cmd, sender, args) -> manager.asyncBackUp(sender);

    @Sub
    public final SubExecutor<ICommandSender> help = (cmd, sender, args) -> {
        if (args.notEmpty()) {
            CommandCore sub = cmd.getParent().getSub(args.first());
            if (sub != null) {
                sub.sendUsage(sender);
            } else {
                manager.sendKey(sender, "noSuchSubCmd", args.first());
            }
        } else {
            LinkedHashSet<CommandCore> subs = new LinkedHashSet<>(cmd.getParent().getSubs());
            subs.remove(cmd);
            for (CommandCore sub : subs) {
                sub.sendUsage(sender);
            }
        }
    };

    @Tab(path = "help")
    public final TabExecutor<ICommandSender> tab_help = (cmd, sender, args) -> args.size() > 1 ? new ArrayList<>() : ListUtils.getMatchList(args.first(), cmd.getSubKeys());

    @Sub(parent = Violet.PLUGIN_ID, perm = "admin", usage = "usage.plugins")
    public final SubExecutor<ICommandSender> plugins = (cmd, sender, args) -> {
        ManagerCore.listPlugins();
    };
}
