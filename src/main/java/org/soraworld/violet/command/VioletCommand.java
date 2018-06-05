package org.soraworld.violet.command;

import org.soraworld.violet.config.VioletManager;
import rikka.api.command.CommandArgs;
import rikka.api.command.ICommandSender;
import rikka.api.command.IICommand;

import static org.soraworld.violet.constant.Violets.*;

public class VioletCommand extends IICommand {

    public VioletCommand(String perm, boolean onlyPlayer, VioletManager manager, String... aliases) {
        super(perm, onlyPlayer, aliases);
        addSub(new IICommand(manager.adminPerm, false, "lang") {
            public boolean execute(ICommandSender sender, CommandArgs args) {
                if (args.notEmpty()) {
                    manager.lang = args.first();
                    manager.save();
                    sender.sendVKey(KEY_SET_LANG, manager.lang);
                } else sender.sendVKey(KEY_GET_LANG, manager.lang);
                return true;
            }
        });
        addSub(new IICommand(manager.adminPerm, false, "save") {
            public boolean execute(ICommandSender sender, CommandArgs args) {
                sender.sendVKey(manager.save() ? KEY_CFG_SAVE : KEY_CFG_SAVE_FAIL);
                return true;
            }
        });
        addSub(new IICommand(manager.adminPerm, false, "debug") {
            public boolean execute(ICommandSender sender, CommandArgs args) {
                manager.debug = !manager.debug;
                sender.sendVKey(manager.debug ? KEY_DEBUG_ON : KEY_DEBUG_OFF);
                return true;
            }
        });
        addSub(new IICommand(manager.adminPerm, false, "reload") {
            public boolean execute(ICommandSender sender, CommandArgs args) {
                sender.sendVKey(manager.load() ? KEY_CFG_LOAD : KEY_CFG_LOAD_FAIL);
                return true;
            }
        });
    }

}
