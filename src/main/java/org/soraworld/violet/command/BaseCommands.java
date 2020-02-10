package org.soraworld.violet.command;

import org.soraworld.violet.Violet;
import org.soraworld.violet.api.ICommandSender;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.core.PluginCore;
import org.soraworld.violet.inject.Cmd;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.inject.Tab;
import org.soraworld.violet.util.ListUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * @author Himmelt
 */
@Cmd(name = Violet.PLUGIN_ID, usage = "/violet lang|save|debug|reload|extract|help|plugins ")
public final class BaseCommands {

    @Inject
    private IPlugin plugin;
    @Inject
    private PluginCore core;

    @Cmd(admin = true, tabs = {"zh_cn", "en_us"})
    public final SubExecutor<ICommandSender> lang = (cmd, sender, args) -> {
        if (args.notEmpty()) {
            String oldLang = core.getLang();
            if (core.setLang(args.first())) {
                plugin.sendMessageKey(sender, "setLang", core.getLang());
                if (!core.getLang().equalsIgnoreCase(oldLang)) {
                    core.asyncSave(null);
                }
            } else {
                plugin.sendMessageKey(sender, "setLangFailed", args.first());
            }
        } else {
            plugin.sendMessageKey(sender, "getLang", core.getLang());
        }
    };

    @Cmd(admin = true)
    public final SubExecutor<ICommandSender> save = (cmd, sender, args) -> {
        if (args.notEmpty()) {
            CommandCore sub = cmd.getSub(args.first());
            if (sub != null) {
                sub.execute(sender, args.next());
            }
        } else {
            core.asyncSave(result -> plugin.sendMessageKey(sender, result ? "configSaved" : "configSaveFailed"));
        }
    };

    @Cmd(admin = true)
    public final SubExecutor<ICommandSender> debug = (cmd, sender, args) -> {
        core.setDebug(!core.isDebug());
        plugin.sendMessageKey(sender, core.isDebug() ? "debugON" : "debugOFF");
    };

    @Cmd(admin = true, tabs = {"lang"})
    public final SubExecutor<ICommandSender> reload = (cmd, sender, args) -> {
        if ("lang".equalsIgnoreCase(args.first())) {
            core.setLang(core.getLang());
            plugin.sendMessageKey(sender, "reloadLang");
        } else {
            plugin.sendMessageKey(sender, core.load() ? "configLoaded" : "configLoadFailed");
        }
    };

    @Cmd(admin = true)
    public final SubExecutor<ICommandSender> extract = (cmd, sender, args) -> plugin.sendMessageKey(sender, core.extract() ? "extracted" : "extractFailed");

    @Cmd(admin = true)
    public final SubExecutor<ICommandSender> backup = (cmd, sender, args) -> core.asyncBackup(result -> plugin.sendMessageKey(sender, result ? "backupSuccess" : "backupFailed"));

    @Cmd
    public final SubExecutor<ICommandSender> help = (cmd, sender, args) -> {
        if (args.notEmpty()) {
            CommandCore sub = cmd.getParent().getSub(args.first());
            if (sub != null) {
                sub.sendUsage(sender);
            } else {
                plugin.sendMessageKey(sender, "noSuchSubCmd", args.first());
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

    @Cmd(plugin = Violet.PLUGIN_ID, admin = true)
    public final SubExecutor<ICommandSender> plugins = (cmd, sender, args) -> PluginCore.listPlugins(sender);
}
