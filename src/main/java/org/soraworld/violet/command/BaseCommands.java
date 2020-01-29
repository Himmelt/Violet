package org.soraworld.violet.command;

import org.soraworld.violet.Violet;
import org.soraworld.violet.api.ICommandSender;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.core.PluginCore;
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
    private IPlugin plugin;

    @Sub(perm = "admin", tabs = {"zh_cn", "en_us"}, usage = "usage.lang")
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

    @Sub(perm = "admin", usage = "usage.save")
    public final SubExecutor<ICommandSender> save = (cmd, sender, args) -> {
        if (args.notEmpty()) {
            CommandCore sub = cmd.getSub(args.first());
            if (sub != null) {
                sub.execute(sender, args.next());
            }
        } else {
            plugin.asyncSave(result -> {

            });
        }
    };

    @Sub(perm = "admin", usage = "usage.debug")
    public final SubExecutor<ICommandSender> debug = (cmd, sender, args) -> {
        plugin.setDebug(!plugin.isDebug());
        sender.sendMessageKey(plugin.isDebug() ? "debugON" : "debugOFF");
    };

    @Sub(perm = "admin", usage = "usage.reload", tabs = {"lang"})
    public final SubExecutor<ICommandSender> reload = (cmd, sender, args) -> {
        if ("lang".equalsIgnoreCase(args.first())) {
            plugin.setLang(plugin.getLang());
            sender.sendMessageKey("reloadLang");
        } else {
            sender.sendMessageKey(plugin.load() ? "configLoaded" : "configLoadFailed");
        }
    };

    @Sub(perm = "admin", usage = "usage.rextract")
    public final SubExecutor<ICommandSender> rextract = (cmd, sender, args) -> sender.sendMessageKey(plugin.extract() ? "reExtracted" : "reExtractFailed");

    @Sub(perm = "admin", usage = "usage.backup")
    public final SubExecutor<ICommandSender> backup = (cmd, sender, args) -> plugin.asyncBackup(result -> {

    });

    @Sub
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

    @Sub(parent = Violet.PLUGIN_ID, perm = "admin", usage = "usage.plugins")
    public final SubExecutor<ICommandSender> plugins = (cmd, sender, args) -> {
        PluginCore.listPlugins();
    };
}
