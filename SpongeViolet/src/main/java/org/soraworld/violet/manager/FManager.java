package org.soraworld.violet.manager;

import org.soraworld.violet.SpongeViolet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.inject.Manager;
import org.soraworld.violet.util.ChatColor;
import org.spongepowered.api.command.CommandSource;

import java.nio.file.Path;
import java.util.HashMap;

/**
 * @author Himmelt
 */
@Manager
public final class FManager extends SpongeManager {

    private static HashMap<String, HashMap<String, String>> langMaps = new HashMap<>();

    public FManager(SpongeViolet plugin, Path path) {
        super(plugin, path);
        translator = (lang, key, args) -> {
            String text = langMaps.computeIfAbsent(lang, this::loadLangMap).get(key);
            if (text == null || text.isEmpty()) {
                return trans(key, args);
            } else {
                return text;
            }
        };
    }

    @Override
    public boolean setLang(String lang) {
        boolean flag = super.setLang(lang);
        langMaps.clear();
        langMaps.put(lang, langMap);
        return flag;
    }

    @Override
    public ChatColor defChatColor() {
        return ChatColor.DARK_PURPLE;
    }

    public void listPlugins(CommandSource sender) {
        for (IPlugin plugin : PLUGINS) {
            sendKey(sender, "pluginInfo", plugin.getId(), plugin.getVersion());
        }
    }
}
