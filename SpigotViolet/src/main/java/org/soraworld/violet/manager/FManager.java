package org.soraworld.violet.manager;

import org.bukkit.command.CommandSender;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.SpigotViolet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.inject.MainManager;
import org.soraworld.violet.util.ChatColor;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Himmelt
 */
@MainManager
public final class FManager extends VManager {

    @Setting(comment = "comment.uuid")
    private UUID uuid = UUID.randomUUID();
    private final Path dataPath;

    private static HashMap<String, HashMap<String, String>> langMaps = new HashMap<>();
    private static final ConcurrentHashMap<UUID, Object> ASYNC_LOCK = new ConcurrentHashMap<>();

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
        dataPath = path.resolve("storedata");
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

    public UUID getUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
            asyncSave(null);
        }
        return uuid;
    }

    public void listPlugins(CommandSender sender) {
        for (IPlugin plugin : PLUGINS) {
            sendKey(sender, "pluginInfo", plugin.getId(), plugin.getVersion());
        }
    }
}
