package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.soraworld.violet.config.IIConfig;
import org.soraworld.violet.constant.Violets;

import java.util.ArrayList;

public class CommandViolet extends IICommand {

    public CommandViolet(String name, String perm, final IIConfig config, final Plugin plugin) {
        super(name, perm, config);
        addSub(new IICommand("lang", config.defaultAdminPerm(), config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    config.sendV(sender, Violets.KEY_GET_LANG, config.getLang());
                } else {
                    config.setLang(args.get(0));
                    config.sendV(sender, Violets.KEY_SET_LANG, config.getLang());
                }
                return true;
            }
        });
        addSub(new IICommand("save", config.defaultAdminPerm(), config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (config.save()) {
                    config.sendV(sender, Violets.KEY_CFG_SAVE);
                } else {
                    config.sendV(sender, Violets.KEY_CFG_SAVE_FAIL);
                }
                return true;
            }
        });
        addSub(new IICommand("debug", config.defaultAdminPerm(), config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (config.debug()) {
                    config.debug(false);
                    config.sendV(sender, Violets.KEY_DEBUG_OFF);
                } else {
                    config.debug(true);
                    config.sendV(sender, Violets.KEY_DEBUG_ON);
                }
                return true;
            }
        });
        addSub(new IICommand("reload", config.defaultAdminPerm(), config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (config.load()) {
                    config.sendV(sender, Violets.KEY_CFG_LOAD);
                } else {
                    config.sendV(sender, Violets.KEY_CFG_LOAD_FAIL);
                }
                return true;
            }
        });
    }

}
