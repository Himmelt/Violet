package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.soraworld.violet.api.IManager;

import static org.soraworld.violet.Violets.*;

public class VioletCommand extends ICommand {

    public VioletCommand(String perm, boolean onlyPlayer, IManager manager, String... aliases) {
        super(perm, onlyPlayer, manager, aliases);
        addSub(new ICommand(manager.defAdminPerm(), false, manager, "lang") {
            public void execute(CommandSender sender, CommandArgs args) {
                if (args.notEmpty()) {
                    manager.setLang(args.first());
                    manager.save();
                    manager.sendKey(sender, KEY_SET_LANG, manager.getLang());
                } else manager.sendKey(sender, KEY_GET_LANG, manager.getLang());
            }
        });
        addSub(new ICommand(manager.defAdminPerm(), false, manager, "save") {
            public void execute(CommandSender sender, CommandArgs args) {
                manager.sendKey(sender, manager.save() ? KEY_CFG_SAVE : KEY_CFG_SAVE_FAIL);
            }
        });
        addSub(new ICommand(manager.defAdminPerm(), false, manager, "debug") {
            public void execute(CommandSender sender, CommandArgs args) {
                manager.setDebug(!manager.isDebug());
                manager.sendKey(sender, manager.isDebug() ? KEY_DEBUG_ON : KEY_DEBUG_OFF);
            }
        });
        addSub(new ICommand(manager.defAdminPerm(), false, manager, "reload") {
            public void execute(CommandSender sender, CommandArgs args) {
                manager.sendKey(sender, manager.load() ? KEY_CFG_LOAD : KEY_CFG_LOAD_FAIL);
            }
        });
    }

    protected void sendUsage(CommandSender sender) {
        manager.sendKey(sender, KEY_CMD_USAGE, "/violet lang|debug|save|reload");
    }

}
