package org.soraworld.violet.manager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.SpigotViolet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.data.DataAPI;
import org.soraworld.violet.inject.MainManager;
import org.soraworld.violet.util.ChatColor;

import java.io.IOException;
import java.nio.file.Files;
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

    /**
     * Load data.
     *
     * @param uuid the uuid
     */
    public void loadData(UUID uuid) {
        FileNode node = new FileNode(dataPath.resolve(uuid.toString() + ".dat").toFile(), DataAPI.options);
        try {
            synchronized (ASYNC_LOCK.computeIfAbsent(uuid, u -> new Object())) {
                node.load(false, true);
                DataAPI.readStore(uuid, node);
            }
            debug("UUID:" + uuid + " store data load success.");
        } catch (Throwable e) {
            console(ChatColor.RED + "UUID:" + uuid + " store data load exception.");
            debug(e);
        }
    }

    public void asyncLoadData(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> loadData(uuid));
    }

    public void saveData(UUID uuid, boolean clear) {
        Path dataFile = dataPath.resolve(uuid.toString() + ".dat");
        if (Files.notExists(dataFile)) {
            try {
                Files.createDirectories(dataFile.getParent());
            } catch (IOException e) {
                debug(e);
            }
        }
        FileNode node = new FileNode(dataFile.toFile(), DataAPI.options);
        try {
            synchronized (ASYNC_LOCK.computeIfAbsent(uuid, u -> new Object())) {
                DataAPI.writeStore(uuid, node);
                node.save();
                if (clear) {
                    DataAPI.clearStore(uuid);
                }
            }
            debug("UUID:" + uuid + " store data save success.");
        } catch (Throwable e) {
            console(ChatColor.RED + "UUID:" + uuid + " store data save exception.");
            debug(e);
        }
    }

    public void asyncSaveData(UUID uuid, boolean clear) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            saveData(uuid, clear);
            if (clear) {
                ASYNC_LOCK.remove(uuid);
            }
        });
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
