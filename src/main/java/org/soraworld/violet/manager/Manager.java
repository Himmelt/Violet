package org.soraworld.violet.manager;

import org.bukkit.ChatColor;
import org.soraworld.violet.Violets;
import org.soraworld.violet.api.IPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashMap;

public class Manager extends VioletManager {

    private static Manager manager;
    private static HashMap<String, HashMap<String, String>> langMaps = new HashMap<>();

    public Manager(IPlugin plugin, Path path, VioletSettings settings) {
        super(plugin, path, settings);
        manager = this;
    }

    public String trans(@Nonnull String key, Object... args) {
        String text = langMap.get(key);
        return (text == null || text.isEmpty()) ? key : args.length > 0 ? String.format(text, args) : text;
    }

    @Nonnull
    public String defChatHead() {
        return "[" + Violets.PLUGIN_NAME + "] ";
    }

    @Nonnull
    public ChatColor defChatColor() {
        return ChatColor.DARK_PURPLE;
    }

    @Nullable
    public String defAdminPerm() {
        return Violets.PERM_ADMIN;
    }

    public void afterLoad() {
    }

    public static String trans(String lang, String key, Object... args) {
        String text = langMaps.computeIfAbsent(lang, s -> manager.loadLangMap(s)).get(key);
        if (text == null || text.isEmpty()) {
            // fallback current lang
            if (manager != null) return manager.trans(key, args);
            else return key;
        } else return text;
    }
}
