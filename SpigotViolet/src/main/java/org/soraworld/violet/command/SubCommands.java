package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.soraworld.violet.manager.SpigotManager;

import static org.soraworld.violet.Violet.*;

public final class SubCommands {

    @Sub(paths = {"lang"}, perm = "admin", aliases = {"language"}, tabs = {"zh_cn", "en_us"}, usage = "/violet lang [code]")
    public static void lang(SpigotManager manager, CommandSender sender, CommandArgs args) {
        if (args.notEmpty()) {
            if (manager.setLang(args.first())) {
                manager.asyncSave();
                manager.sendKey(sender, KEY_SET_LANG, manager.getLang());
            } else {
                manager.sendKey(sender, KEY_SET_LANG_FAILED, args.first());
            }
        } else manager.sendKey(sender, KEY_GET_LANG, manager.getLang());
    }

    @Sub(perm = "admin")
    public static void save(SpigotManager manager, CommandSender sender, CommandArgs args) {
        manager.sendKey(sender, manager.save() ? KEY_CFG_SAVE : KEY_CFG_SAVE_FAIL);
    }

    @Sub(perm = "admin")
    public static void debug(SpigotManager manager, CommandSender sender, CommandArgs args) {
        manager.setDebug(!manager.isDebug());
        manager.sendKey(sender, manager.isDebug() ? KEY_DEBUG_ON : KEY_DEBUG_OFF);
    }

    @Sub
    public static void reload(SpigotManager manager, CommandSender sender, CommandArgs args) {
        manager.sendKey(sender, manager.load() ? KEY_CFG_LOAD : KEY_CFG_LOAD_FAIL);
    }
}
