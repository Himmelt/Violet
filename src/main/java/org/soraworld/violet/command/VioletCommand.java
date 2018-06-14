package org.soraworld.violet.command;

import org.soraworld.violet.config.VioletManager;
import org.soraworld.violet.config.VioletSetting;
import rikka.api.command.CommandArgs;
import rikka.api.command.ExecuteResult;
import rikka.api.command.ICommandSender;
import rikka.api.command.IICommand;

import static org.soraworld.violet.constant.Violets.*;
import static rikka.api.command.ExecuteResult.SUCCESS;

public class VioletCommand extends IICommand {

    public VioletCommand(String perm, boolean onlyPlayer, VioletManager manager, String... aliases) {
        super(perm, onlyPlayer, aliases);
        final VioletSetting setting = manager.getSetting();
        addSub(new IICommand(manager.adminPerm(), false, "lang") {
            public ExecuteResult execute(ICommandSender sender, CommandArgs args) {
                if (args.notEmpty()) {
                    setting.lang = args.first();
                    manager.save();
                    manager.sendVKey(sender, KEY_SET_LANG, setting.lang);
                } else manager.sendVKey(sender, KEY_GET_LANG, setting.lang);
                return SUCCESS;
            }
        });
        addSub(new IICommand(manager.adminPerm(), false, "save") {
            public ExecuteResult execute(ICommandSender sender, CommandArgs args) {
                manager.sendVKey(sender, manager.save() ? KEY_CFG_SAVE : KEY_CFG_SAVE_FAIL);
                return SUCCESS;
            }
        });
        addSub(new IICommand(manager.adminPerm(), false, "debug") {
            public ExecuteResult execute(ICommandSender sender, CommandArgs args) {
                setting.debug = !setting.debug;
                manager.sendVKey(sender, setting.debug ? KEY_DEBUG_ON : KEY_DEBUG_OFF);
                return SUCCESS;
            }
        });
        addSub(new IICommand(manager.adminPerm(), false, "reload") {
            public ExecuteResult execute(ICommandSender sender, CommandArgs args) {
                manager.sendVKey(sender, manager.load() ? KEY_CFG_LOAD : KEY_CFG_LOAD_FAIL);
                return SUCCESS;
            }
        });
    }

}
