package org.soraworld.violet.command;

import org.soraworld.violet.manager.SpongeManager;
import org.spongepowered.api.command.CommandSource;

import static org.soraworld.violet.Violet.*;

public final class SpongeBaseSubs {
    @Sub(perm = "admin", tabs = {"zh_cn", "en_us"})
    public static void lang(SpongeManager manager, CommandSource sender, CommandArgs args) {
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
    public static void save(SpongeManager manager, CommandSource sender, CommandArgs args) {
        manager.sendKey(sender, manager.save() ? KEY_CFG_SAVE : KEY_CFG_SAVE_FAIL);
    }

    @Sub(perm = "admin")
    public static void debug(SpongeManager manager, CommandSource sender, CommandArgs args) {
        manager.setDebug(!manager.isDebug());
        manager.sendKey(sender, manager.isDebug() ? KEY_DEBUG_ON : KEY_DEBUG_OFF);
    }

    @Sub(perm = "admin")
    public static void reload(SpongeManager manager, CommandSource sender, CommandArgs args) {
        manager.sendKey(sender, manager.load() ? KEY_CFG_LOAD : KEY_CFG_LOAD_FAIL);
    }

    @Sub(perm = "admin")
    public static void plugins(SpongeManager manager, CommandSource sender, CommandArgs args) {
        if (manager instanceof SpongeManager.Manager) {
            ((SpongeManager.Manager) manager).listPlugins(sender);
        }
    }
}
