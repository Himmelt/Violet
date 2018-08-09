package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.soraworld.violet.config.VioletManager;

import static org.soraworld.violet.constant.Violets.*;

public class VioletCommand extends IICommand {

    public VioletCommand(String perm, boolean onlyPlayer, VioletManager manager, String... aliases) {
        super(perm, onlyPlayer, aliases);
        addSub(new IICommand(manager.adminPerm(), false, "lang") {
            public void execute(CommandSender sender, CommandArgs args) {
                if (args.notEmpty()) {
                    manager.setLang(args.first());
                    manager.save();
                    manager.sendKey(sender, KEY_SET_LANG, manager.lang());
                } else manager.sendKey(sender, KEY_GET_LANG, manager.lang());
            }
        });
        addSub(new IICommand(manager.adminPerm(), false, "save") {
            public void execute(CommandSender sender, CommandArgs args) {
                manager.sendKey(sender, manager.save() ? KEY_CFG_SAVE : KEY_CFG_SAVE_FAIL);
            }
        });
        addSub(new IICommand(manager.adminPerm(), false, "debug") {
            public void execute(CommandSender sender, CommandArgs args) {
                manager.debug(!manager.debug());
                manager.sendKey(sender, manager.debug() ? KEY_DEBUG_ON : KEY_DEBUG_OFF);
            }
        });
        addSub(new IICommand(manager.adminPerm(), false, "reload") {
            public void execute(CommandSender sender, CommandArgs args) {
                manager.sendKey(sender, manager.load() ? KEY_CFG_LOAD : KEY_CFG_LOAD_FAIL);
            }
        });
    }

}
