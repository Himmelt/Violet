package org.soraworld.violet.manager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.SpigotViolet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.data.DataAPI;
import org.soraworld.violet.util.ChatColor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Violet 管理器.
 */
public final class FBManager extends SpigotManager {

    @Setting(comment = "comment.uuid")
    private UUID uuid = UUID.randomUUID();
    private final Path dataPath;

    private static FBManager manager;
    private static HashMap<String, HashMap<String, String>> langMaps = new HashMap<>();
    private static final ConcurrentHashMap<UUID, Boolean> asyncDataLock = new ConcurrentHashMap<>();

    /**
     * 实例化管理器.
     *
     * @param plugin 插件实例
     * @param path   配置保存路径
     */
    public FBManager(SpigotViolet plugin, Path path) {
        super(plugin, path);
        manager = this;
        dataPath = path.resolve("storedata");
    }

    public boolean setLang(String lang) {
        boolean flag = super.setLang(lang);
        langMaps.clear();
        langMaps.put(lang, langMap);
        return flag;
    }

    public void loadData(UUID uuid) {
        FileNode node = new FileNode(dataPath.resolve(uuid.toString() + ".dat").toFile(), DataAPI.options);
        try {
            node.load(false);
            DataAPI.readStore(uuid, node);
            debug("UUID:" + uuid + " store data async load success.");
        } catch (Exception e) {
            console(ChatColor.RED + "UUID:" + uuid + " store data async load exception.");
            debug(e);
        }
    }

    public void asyncLoadData(UUID uuid) {
        if (!asyncDataLock.getOrDefault(uuid, false)) {
            asyncDataLock.put(uuid, true);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                loadData(uuid);
                asyncDataLock.put(uuid, false);
            });
        }
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
        DataAPI.writeStore(uuid, node);
        try {
            node.save();
            if (clear) DataAPI.clearStore(uuid);
            debug("UUID:" + uuid + " store data async save success.");
        } catch (Exception e) {
            console(ChatColor.RED + "UUID:" + uuid + " store data async save exception.");
            debug(e);
        }
    }

    public void asyncSaveData(UUID uuid, boolean clear) {
        if (!asyncDataLock.getOrDefault(uuid, false)) {
            asyncDataLock.put(uuid, true);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                saveData(uuid, clear);
                asyncDataLock.put(uuid, false);
                if (clear) asyncDataLock.remove(uuid);
            });
        }
    }

    public String trans(String key, Object... args) {
        String text = langMap.get(key);
        return (text == null || text.isEmpty()) ? key : args.length > 0 ? String.format(text, args) : text;
    }

    public ChatColor defChatColor() {
        return ChatColor.DARK_PURPLE;
    }

    /**
     * 全局翻译.
     *
     * @param lang 翻译语言
     * @param key  键
     * @param args 参数
     * @return 全局翻译结果
     */
    public static String trans(String lang, String key, Object... args) {
        if (manager == null) return key;
        String text = langMaps.computeIfAbsent(lang, s -> manager.loadLangMap(s)).get(key);
        if (text == null || text.isEmpty()) {
            return manager.trans(key, args);
        } else return text;
    }

    /**
     * 向命令发送者显示 violet 插件列表.
     *
     * @param sender 命令发送者
     */
    public void listPlugins(CommandSender sender) {
        for (IPlugin plugin : plugins) {
            sendKey(sender, "pluginInfo", plugin.getId(), plugin.getVersion());
        }
    }

    /**
     * 获取 Violet 插件运行 uuid
     *
     * @return the uuid
     */
    public UUID getUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
            asyncSave();
        }
        return uuid;
    }
}
