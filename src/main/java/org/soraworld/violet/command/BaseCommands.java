package org.soraworld.violet.command;

import org.soraworld.violet.Violet;
import org.soraworld.violet.api.ICommandSender;
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
    private PluginCore plugin;

    @Cmd(admin = true, tabs = {"zh_cn", "en_us"})
    public final SubExecutor<ICommandSender> lang = (cmd, sender, args) -> {
        if (args.notEmpty()) {
            String oldLang = plugin.getLang();
            if (plugin.setLang(args.first())) {
                sender.sendMessageKey("setLang", plugin.getLang());
                if (!plugin.getLang().equalsIgnoreCase(oldLang)) {
                    plugin.asyncSave(null);
                }
            } else {
                sender.sendMessageKey("setLangFailed", args.first());
            }
        } else {
            sender.sendMessageKey("getLang", plugin.getLang());
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
            plugin.asyncSave(result -> sender.sendMessageKey(result ? "configSaved" : "configSaveFailed"));
        }
    };

    @Cmd(admin = true)
    public final SubExecutor<ICommandSender> debug = (cmd, sender, args) -> {
        plugin.setDebug(!plugin.isDebug());
        sender.sendMessageKey(plugin.isDebug() ? "debugON" : "debugOFF");
    };

    @Cmd(admin = true, tabs = {"lang"})
    public final SubExecutor<ICommandSender> reload = (cmd, sender, args) -> {
        if ("lang".equalsIgnoreCase(args.first())) {
            plugin.setLang(plugin.getLang());
            sender.sendMessageKey("reloadLang");
        } else {
            sender.sendMessageKey(plugin.load() ? "configLoaded" : "configLoadFailed");
        }
    };

    @Cmd(admin = true)
    public final SubExecutor<ICommandSender> extract = (cmd, sender, args) -> sender.sendMessageKey(plugin.extract() ? "extracted" : "extractFailed");

    @Cmd(admin = true)
    public final SubExecutor<ICommandSender> backup = (cmd, sender, args) -> plugin.asyncBackup(result -> sender.sendMessageKey(result ? "backupSuccess" : "backupFailed"));

    @Cmd
    public final SubExecutor<ICommandSender> help = (cmd, sender, args) -> {
        if (args.notEmpty()) {
            CommandCore sub = cmd.getParent().getSub(args.first());
            if (sub != null) {
                sub.sendUsage(sender);
            } else {
                sender.sendMessageKey("noSuchSubCmd", args.first());
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
