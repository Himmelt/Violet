package org.soraworld.violet.manager;

import org.bukkit.command.CommandSender;
import org.soraworld.violet.SpigotViolet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.inject.MainManager;
import org.soraworld.violet.util.ChatColor;

import java.nio.file.Path;
import java.util.HashMap;

/**
 * @author Himmelt
 */
@MainManager
public final class FManager extends VManager {

    private static HashMap<String, HashMap<String, String>> langMaps = new HashMap<>();

    public FManager(SpigotViolet plugin, Path path) {
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

    public void listPlugins(CommandSender sender) {
        for (IPlugin plugin : PLUGINS) {
            sendKey(sender, "pluginInfo", plugin.getId(), plugin.getVersion());
        }
    }
}
