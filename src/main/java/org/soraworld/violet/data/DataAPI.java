package org.soraworld.violet.data;

import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Options;
import org.soraworld.violet.serializers.UUIDSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DataAPI {

    public static final Options options = Options.build();
    private static final ConcurrentHashMap<UUID, HashMap<String, Object>> playerTempData = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, HashMap<String, Object>> playerStoreData = new ConcurrentHashMap<>();

    static {
        options.registerType(new UUIDSerializer());
    }

    /* Temp Data */

    public static boolean hasTemp(UUID uuid, String key) {
        Map<String, Object> data = playerTempData.get(uuid);
        return data != null && data.containsKey(key);
    }

    public static boolean hasTemp(UUID uuid, String key, Class<?> clazz) {
        Map<String, Object> data = playerTempData.get(uuid);
        if (data != null) {
            Object obj = data.get(key);
            return obj != null && clazz.isAssignableFrom(obj.getClass());
        }
        return false;
    }

    public static <T> T getTemp(UUID uuid, String key, T def, Class<T> clazz) {
        Map<String, Object> data = playerTempData.get(uuid);
        if (data != null) {
            Object obj = data.getOrDefault(key, def);
            if (obj != null && clazz.isAssignableFrom(obj.getClass())) {
                return (T) obj;
            }
        }
        return def;
    }

    public static <T> T getTemp(UUID uuid, String key, Class<T> clazz) {
        return getTemp(uuid, key, null, clazz);
    }

    public static Object getTemp(UUID uuid, String key) {
        Map<String, Object> data = playerTempData.get(uuid);
        if (data != null) return data.get(key);
        return null;
    }

    public static boolean getTempBool(UUID uuid, String key, boolean def) {
        return getTemp(uuid, key, def, boolean.class);
    }

    public static boolean getTempBool(UUID uuid, String key) {
        return getTemp(uuid, key, false, boolean.class);
    }

    public static byte getTempByte(UUID uuid, String key, byte def) {
        return getTemp(uuid, key, def, byte.class);
    }

    public static byte getTempByte(UUID uuid, String key) {
        return getTemp(uuid, key, (byte) 0, byte.class);
    }

    public static int getTempInt(UUID uuid, String key, int def) {
        return getTemp(uuid, key, def, int.class);
    }

    public static int getTempInt(UUID uuid, String key) {
        return getTemp(uuid, key, 0, int.class);
    }

    public static long getTempLong(UUID uuid, String key, long def) {
        return getTemp(uuid, key, def, long.class);
    }

    public static long getTempLong(UUID uuid, String key) {
        return getTemp(uuid, key, 0L, long.class);
    }

    public static float getTempFloat(UUID uuid, String key, float def) {
        return getTemp(uuid, key, def, float.class);
    }

    public static float getTempFloat(UUID uuid, String key) {
        return getTemp(uuid, key, 0F, float.class);
    }

    public static double getTempDouble(UUID uuid, String key, double def) {
        return getTemp(uuid, key, def, double.class);
    }

    public static double getTempDouble(UUID uuid, String key) {
        return getTemp(uuid, key, 0D, double.class);
    }

    public static String getTempString(UUID uuid, String key, String def) {
        return getTemp(uuid, key, def, String.class);
    }

    public static String getTempString(UUID uuid, String key) {
        return getTemp(uuid, key, null, String.class);
    }

    public static void setTemp(UUID uuid, String key, Object value) {
        playerTempData.computeIfAbsent(uuid, u -> new HashMap<>()).put(key, value);
    }

    public static Object removeTemp(UUID uuid, String key) {
        Map<String, Object> data = playerTempData.get(uuid);
        if (data != null) return data.remove(key);
        return null;
    }

    public static void clearTemp(UUID uuid) {
        playerTempData.remove(uuid);
    }

    /* Store Data */

    public static boolean hasStore(UUID uuid, String key) {
        Map<String, Object> data = playerStoreData.get(uuid);
        return data != null && data.containsKey(key);
    }

    public static boolean hasStore(UUID uuid, String key, Class<?> clazz) {
        Map<String, Object> data = playerStoreData.get(uuid);
        if (data != null) {
            Object obj = data.get(key);
            return obj != null && clazz.isAssignableFrom(obj.getClass());
        }
        return false;
    }

    public static <T> T getStore(UUID uuid, String key, T def, Class<T> clazz) {
        Map<String, Object> data = playerStoreData.get(uuid);
        if (data != null) {
            Object obj = data.getOrDefault(key, def);
            if (obj != null && clazz.isAssignableFrom(obj.getClass())) {
                return (T) obj;
            }
        }
        return def;
    }

    public static <T> T getStore(UUID uuid, String key, Class<T> clazz) {
        return getStore(uuid, key, null, clazz);
    }

    public static Object getStore(UUID uuid, String key) {
        Map<String, Object> data = playerStoreData.get(uuid);
        if (data != null) return data.get(key);
        return null;
    }

    public static boolean getStoreBool(UUID uuid, String key, boolean def) {
        return getStore(uuid, key, def, boolean.class);
    }

    public static boolean getStoreBool(UUID uuid, String key) {
        return getStore(uuid, key, false, boolean.class);
    }

    public static byte getStoreByte(UUID uuid, String key, byte def) {
        return getStore(uuid, key, def, byte.class);
    }

    public static byte getStoreByte(UUID uuid, String key) {
        return getStore(uuid, key, (byte) 0, byte.class);
    }

    public static int getStoreInt(UUID uuid, String key, int def) {
        return getStore(uuid, key, def, int.class);
    }

    public static int getStoreInt(UUID uuid, String key) {
        return getStore(uuid, key, 0, int.class);
    }

    public static long getStoreLong(UUID uuid, String key, long def) {
        return getStore(uuid, key, def, long.class);
    }

    public static long getStoreLong(UUID uuid, String key) {
        return getStore(uuid, key, 0L, long.class);
    }

    public static float getStoreFloat(UUID uuid, String key, float def) {
        return getStore(uuid, key, def, float.class);
    }

    public static float getStoreFloat(UUID uuid, String key) {
        return getStore(uuid, key, 0F, float.class);
    }

    public static double getStoreDouble(UUID uuid, String key, double def) {
        return getStore(uuid, key, def, double.class);
    }

    public static double getStoreDouble(UUID uuid, String key) {
        return getStore(uuid, key, 0D, double.class);
    }

    public static String getStoreString(UUID uuid, String key, String def) {
        return getStore(uuid, key, def, String.class);
    }

    public static String getStoreString(UUID uuid, String key) {
        return getStore(uuid, key, null, String.class);
    }

    public static void setStore(UUID uuid, String key, Object value) {
        playerStoreData.computeIfAbsent(uuid, u -> new HashMap<>()).put(key, value);
    }

    public static Object removeStore(UUID uuid, String key) {
        Map<String, Object> data = playerStoreData.get(uuid);
        if (data != null) return data.remove(key);
        return null;
    }

    public static void clearStore(UUID uuid) {
        playerStoreData.remove(uuid);
    }

    public static void readStore(UUID uuid, FileNode node) {
        playerStoreData.put(uuid, node.toTypeMap());
    }

    public static void writeStore(UUID uuid, FileNode node) {
        Map<String, Object> map = playerStoreData.get(uuid);
        if (map != null) node.fromTypeMap(map);
    }
}
