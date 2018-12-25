package org.soraworld.violet.data;

import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Node;
import org.soraworld.hocon.node.NodeMap;
import org.soraworld.hocon.node.Options;
import org.soraworld.violet.serializers.UUIDSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DataAPI {

    public static final Options options = Options.build();
    private static final ConcurrentHashMap<UUID, HashMap<String, Object>> playerTempData = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, NodeMap> playerStoreData = new ConcurrentHashMap<>();

    static {
        options.registerType(new UUIDSerializer());
    }

    public static Object hasPlayerTemp(UUID uuid, String key) {
        Map<String, Object> data = playerTempData.get(uuid);
        if (data != null) return data.containsKey(key);
        return false;
    }

    public static Object getPlayerTemp(UUID uuid, String key, Object def) {
        Map<String, Object> data = playerTempData.get(uuid);
        if (data != null) return data.getOrDefault(key, def);
        return def;
    }

    public static void setPlayerTemp(UUID uuid, String key, Object value) {
        playerTempData.computeIfAbsent(uuid, u -> new HashMap<>()).put(key, value);
    }

    public static Object removePlayerTemp(UUID uuid, String key) {
        Map<String, Object> data = playerTempData.get(uuid);
        if (data != null) return data.remove(key);
        return null;
    }

    public static void clearPlayerTemp(UUID uuid) {
        playerTempData.remove(uuid);
    }

    public static boolean hasPlayerStore(UUID uuid, String key) {
        NodeMap data = playerStoreData.get(uuid);
        if (data != null) return data.containsKey(key);
        return false;
    }

    public static Node getPlayerStore(UUID uuid, String key, Node def) {
        NodeMap data = playerStoreData.get(uuid);
        if (data != null) return data.getOrDefault(key, def);
        return def;
    }

    public static void setPlayerStore(UUID uuid, String key, Node value) {
        playerStoreData.computeIfAbsent(uuid, u -> new NodeMap(Options.defaults())).set(key, value);
    }

    public static Node removePlayerStore(UUID uuid, String key) {
        NodeMap data = playerStoreData.get(uuid);
        if (data != null) return data.remove(key);
        return null;
    }

    public static void clearPlayerStore(UUID uuid) {
        playerStoreData.remove(uuid);
    }

    public static void readStore(UUID uuid, FileNode node) {
        playerStoreData.put(uuid, node);
    }

    public static void writeStore(UUID uuid, FileNode node) {
        node.clear();
        NodeMap map = playerStoreData.get(uuid);
        if (map != null) node.putAll(map);
    }
}
