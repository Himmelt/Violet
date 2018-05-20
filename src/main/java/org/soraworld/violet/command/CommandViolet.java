package org.soraworld.violet.command;

import org.soraworld.violet.config.VioletManager;
import org.soraworld.violet.constant.Violets;
import rikka.api.command.ICommandSender;

import java.util.ArrayList;

public class CommandViolet extends IICommand {

    public CommandViolet(String perm, boolean onlyPlayer, VioletManager manager, String... aliases) {
        super(perm, onlyPlayer, manager, aliases);
        addSub(new IICommand(manager.adminPerm, false, manager, "lang") {
            public boolean execute(ICommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    manager.vSendKey(sender, Violets.KEY_GET_LANG, manager.lang);
                } else {
                    manager.lang = args.get(0);
                    manager.save();
                    manager.vSendKey(sender, Violets.KEY_SET_LANG, manager.lang);
                }
                return true;
            }
        });
        addSub(new IICommand(manager.adminPerm, false, manager, "save") {
            public boolean execute(ICommandSender sender, ArrayList<String> args) {
                if (manager.save()) {
                    manager.vSendKey(sender, Violets.KEY_CFG_SAVE);
                } else {
                    manager.vSendKey(sender, Violets.KEY_CFG_SAVE_FAIL);
                }
                return true;
            }
        });
        addSub(new IICommand(manager.adminPerm, false, manager, "debug") {
            public boolean execute(ICommandSender sender, ArrayList<String> args) {
                if (manager.debug) {
                    manager.debug = false;
                    manager.vSendKey(sender, Violets.KEY_DEBUG_OFF);
                } else {
                    manager.debug = true;
                    manager.vSendKey(sender, Violets.KEY_DEBUG_ON);
                }
                return true;
            }
        });
        addSub(new IICommand(manager.adminPerm, false, manager, "reload") {
            public boolean execute(ICommandSender sender, ArrayList<String> args) {
                if (manager.load()) {
                    manager.vSendKey(sender, Violets.KEY_CFG_LOAD);
                } else {
                    manager.vSendKey(sender, Violets.KEY_CFG_LOAD_FAIL);
                }
                return true;
            }
        });
    }

}
