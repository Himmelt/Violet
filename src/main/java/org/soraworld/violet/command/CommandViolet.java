package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.soraworld.violet.Violet;
import org.soraworld.violet.config.IIConfig;

import java.util.ArrayList;

public class CommandViolet extends IICommand {

    public CommandViolet(String name, final IIConfig config, final Plugin plugin) {
        super(name);
        addSub(new IICommand("save") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (config.save()) {
                    config.iiChat.send(sender, Violet.translate("configSaved"));
                } else {
                    config.iiChat.send(sender, Violet.translate("configSaveFailed"));
                }
                return true;
            }
        });
        addSub(new IICommand("reload") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (config.load()) {
                    config.iiChat.send(sender, Violet.translate("configLoaded"));
                } else {
                    config.iiChat.send(sender, Violet.translate("configLoadFailed"));
                }
                return true;
            }
        });
        addSub(new IICommand("lang") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    config.iiChat.send(sender, Violet.translate("getLang", config.getLang()));
                } else {
                    config.setLang(args.get(0));
                    config.iiChat.send(sender, Violet.translate("setLang", config.getLang()));
                }
                return true;
            }
        });
        addSub(new IICommand("debug") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (config.debug()) {
                    config.debug(false);
                    config.iiChat.send(sender, Violet.translate("debugOFF"));
                } else {
                    config.debug(true);
                    config.iiChat.send(sender, Violet.translate("debugON"));
                }
                return true;
            }
        });
    }

}
