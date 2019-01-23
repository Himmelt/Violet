package org.soraworld.violet.manager;

import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.SpongeViolet;
import org.soraworld.violet.data.DataAPI;
import org.soraworld.violet.util.ChatColor;
import org.spongepowered.api.Sponge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Violet 管理器.
 */
public final class FSManager extends SpongeManager {

    @Setting(comment = "comment.uuid")
    private UUID uuid = UUID.randomUUID();
    private final Path dataPath;

    private static final ConcurrentHashMap<UUID, Object> asyncLock = new ConcurrentHashMap<>();
    private static HashMap<String, HashMap<String, String>> langMaps = new HashMap<>();

    /**
     * 实例化管理器.
     *
     * @param plugin 插件实例
     * @param path   配置保存路径
     */
    public FSManager(SpongeViolet plugin, Path path) {
        super(plugin, path);
        translator = (lang, key, args) -> {
            String text = langMaps.computeIfAbsent(lang, this::loadLangMap).get(key);
            if (text == null || text.isEmpty()) {
                return trans(key, args);
            } else return text;
        };
        dataPath = path.resolve("storedata");
    }

    public boolean setLang(String lang) {
        boolean flag = super.setLang(lang);
        langMaps.clear();
        langMaps.put(lang, langMap);
        return flag;
    }

    public ChatColor defChatColor() {
        return ChatColor.DARK_PURPLE;
    }

    public void loadData(UUID uuid) {
        FileNode node = new FileNode(dataPath.resolve(uuid.toString() + ".dat").toFile(), DataAPI.options);
        try {
            synchronized (asyncLock.computeIfAbsent(uuid, u -> new Object())) {
                node.load(false, true);
                DataAPI.readStore(uuid, node);
            }
            debug("UUID:" + uuid + " store data load success.");
        } catch (Exception e) {
            console(ChatColor.RED + "UUID:" + uuid + " store data load exception.");
            debug(e);
        }
    }

    public void asyncLoadData(UUID uuid) {
        Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> loadData(uuid));
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
            synchronized (asyncLock.computeIfAbsent(uuid, u -> new Object())) {
                DataAPI.writeStore(uuid, node);
                node.save();
                if (clear) DataAPI.clearStore(uuid);
            }
            debug("UUID:" + uuid + " store data save success.");
        } catch (Exception e) {
            console(ChatColor.RED + "UUID:" + uuid + " store data save exception.");
            debug(e);
        }
    }

    public void asyncSaveData(UUID uuid, boolean clear) {
        Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> {
            saveData(uuid, clear);
            if (clear) asyncLock.remove(uuid);
        });
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
