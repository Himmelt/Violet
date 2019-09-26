package org.soraworld.violet.data;

import org.jetbrains.annotations.NotNull;
import org.soraworld.hocon.exception.SerializerException;
import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Options;
import org.soraworld.violet.serializers.UUIDSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DataAPI {

    public static final Options options = Options.build();
    private static final ConcurrentHashMap<UUID, HashMap<String, Object>> tempData = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, HashMap<String, Object>> storeData = new ConcurrentHashMap<>();

    static {
        try {
            options.registerType(new UUIDSerializer());
        } catch (SerializerException e) {
            System.out.println("TypeSerializer for UUID register failed");
            e.printStackTrace();
        }
    }

    /**
     * Has temp boolean.
     *
     * @param uuid the uuid
     * @param key  the key
     * @return the boolean
     */
    public static boolean hasTemp(@NotNull UUID uuid, String key) {
        Map<String, Object> data = tempData.get(uuid);
        return data != null && data.containsKey(key);
    }

    public static boolean hasTemp(UUID uuid, String key, Class<?> clazz) {
        Map<String, Object> data = tempData.get(uuid);
        if (data != null) {
            Object obj = data.get(key);
            return obj != null && clazz.isAssignableFrom(obj.getClass());
        }
        return false;
    }

    public static <T> T getTemp(UUID uuid, String key, T def, Class<T> clazz) {
        Map<String, Object> data = tempData.get(uuid);
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

    public static <T> T getTemp(UUID uuid, String key, T def) {
        Map<String, Object> data = tempData.get(uuid);
        if (data != null) {
            Object obj = data.getOrDefault(key, def);
            if (obj != null && def != null && def.getClass().isAssignableFrom(obj.getClass())) {
                return (T) obj;
            }
        }
        return def;
    }

    public static Object getTemp(UUID uuid, String key) {
        Map<String, Object> data = tempData.get(uuid);
        if (data != null) {
            return data.get(key);
        }
        return null;
    }

    public static boolean getTempBool(UUID uuid, String key, boolean def) {
        return getTemp(uuid, key, def, Boolean.class);
    }

    public static boolean getTempBool(UUID uuid, String key) {
        return getTemp(uuid, key, false, Boolean.class);
    }

    public static byte getTempByte(UUID uuid, String key, byte def) {
        return getTemp(uuid, key, def, Byte.class);
    }

    public static byte getTempByte(UUID uuid, String key) {
        return getTemp(uuid, key, (byte) 0, Byte.class);
    }

    public static int getTempInt(UUID uuid, String key, int def) {
        return getTemp(uuid, key, def, Integer.class);
    }

    public static int getTempInt(UUID uuid, String key) {
        return getTemp(uuid, key, 0, Integer.class);
    }

    public static long getTempLong(UUID uuid, String key, long def) {
        return getTemp(uuid, key, def, Long.class);
    }

    public static long getTempLong(UUID uuid, String key) {
        return getTemp(uuid, key, 0L, Long.class);
    }

    public static float getTempFloat(UUID uuid, String key, float def) {
        return getTemp(uuid, key, def, Float.class);
    }

    public static float getTempFloat(UUID uuid, String key) {
        return getTemp(uuid, key, 0F, Float.class);
    }

    public static double getTempDouble(UUID uuid, String key, double def) {
        return getTemp(uuid, key, def, Double.class);
    }

    public static double getTempDouble(UUID uuid, String key) {
        return getTemp(uuid, key, 0D, Double.class);
    }

    public static String getTempString(UUID uuid, String key, String def) {
        return getTemp(uuid, key, def, String.class);
    }

    public static String getTempString(UUID uuid, String key) {
        return getTemp(uuid, key, null, String.class);
    }

    public static void setTemp(UUID uuid, String key, Object value) {
        tempData.computeIfAbsent(uuid, u -> new HashMap<>()).put(key, value);
    }

    public static void setTempBool(UUID uuid, String key, boolean value) {
        setTemp(uuid, key, value);
    }

    public static void setTempByte(UUID uuid, String key, byte value) {
        setTemp(uuid, key, value);
    }

    public static void setTempInt(UUID uuid, String key, int value) {
        setTemp(uuid, key, value);
    }

    public static void setTempLong(UUID uuid, String key, long value) {
        setTemp(uuid, key, value);
    }

    public static void setTempFloat(UUID uuid, String key, float value) {
        setTemp(uuid, key, value);
    }

    public static void setTempDouble(UUID uuid, String key, double value) {
        setTemp(uuid, key, value);
    }

    public static void setTempString(UUID uuid, String key, String value) {
        setTemp(uuid, key, value);
    }

    public static Object removeTemp(UUID uuid, String key) {
        Map<String, Object> data = tempData.get(uuid);
        if (data != null) {
            return data.remove(key);
        }
        return null;
    }

    public static void clearTemp(UUID uuid) {
        tempData.remove(uuid);
    }

    /* Store Data */

    public static boolean hasStore(UUID uuid, String key) {
        Map<String, Object> data = storeData.get(uuid);
        return data != null && data.containsKey(key);
    }

    public static boolean hasStore(UUID uuid, String key, Class<?> clazz) {
        Map<String, Object> data = storeData.get(uuid);
        if (data != null) {
            Object obj = data.get(key);
            return obj != null && clazz.isAssignableFrom(obj.getClass());
        }
        return false;
    }

    public static <T> T getStore(UUID uuid, String key, T def, Class<T> clazz) {
        Map<String, Object> data = storeData.get(uuid);
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

    public static <T> T getStore(UUID uuid, String key, T def) {
        Map<String, Object> data = storeData.get(uuid);
        if (data != null) {
            Object obj = data.getOrDefault(key, def);
            if (obj != null && def != null && def.getClass().isAssignableFrom(obj.getClass())) {
                return (T) obj;
            }
        }
        return def;
    }

    public static Object getStore(UUID uuid, String key) {
        Map<String, Object> data = storeData.get(uuid);
        if (data != null) {
            return data.get(key);
        }
        return null;
    }

    public static boolean getStoreBool(UUID uuid, String key, boolean def) {
        return getStore(uuid, key, def, Boolean.class);
    }

    public static boolean getStoreBool(UUID uuid, String key) {
        return getStore(uuid, key, false, Boolean.class);
    }

    public static byte getStoreByte(UUID uuid, String key, byte def) {
        return getStore(uuid, key, def, Byte.class);
    }

    public static byte getStoreByte(UUID uuid, String key) {
        return getStore(uuid, key, (byte) 0, Byte.class);
    }

    public static int getStoreInt(UUID uuid, String key, int def) {
        return getStore(uuid, key, def, Integer.class);
    }

    public static int getStoreInt(UUID uuid, String key) {
        return getStore(uuid, key, 0, Integer.class);
    }

    public static long getStoreLong(UUID uuid, String key, long def) {
        return getStore(uuid, key, def, Long.class);
    }

    public static long getStoreLong(UUID uuid, String key) {
        return getStore(uuid, key, 0L, Long.class);
    }

    public static float getStoreFloat(UUID uuid, String key, float def) {
        return getStore(uuid, key, def, Float.class);
    }

    public static float getStoreFloat(UUID uuid, String key) {
        return getStore(uuid, key, 0F, Float.class);
    }

    public static double getStoreDouble(UUID uuid, String key, double def) {
        return getStore(uuid, key, def, Double.class);
    }

    public static double getStoreDouble(UUID uuid, String key) {
        return getStore(uuid, key, 0D, Double.class);
    }

    public static String getStoreString(UUID uuid, String key, String def) {
        return getStore(uuid, key, def, String.class);
    }

    public static String getStoreString(UUID uuid, String key) {
        return getStore(uuid, key, null, String.class);
    }

    public static void setStore(UUID uuid, String key, Object value) {
        storeData.computeIfAbsent(uuid, u -> new HashMap<>()).put(key, value);
    }

    public static void setStoreBool(UUID uuid, String key, boolean value) {
        setStore(uuid, key, value);
    }

    public static void setStoreByte(UUID uuid, String key, byte value) {
        setStore(uuid, key, value);
    }

    public static void setStoreInt(UUID uuid, String key, int value) {
        setStore(uuid, key, value);
    }

    public static void setStoreLong(UUID uuid, String key, long value) {
        setStore(uuid, key, value);
    }

    public static void setStoreFloat(UUID uuid, String key, float value) {
        setStore(uuid, key, value);
    }

    public static void setStoreDouble(UUID uuid, String key, double value) {
        setStore(uuid, key, value);
    }

    public static void setStoreString(UUID uuid, String key, String value) {
        setStore(uuid, key, value);
    }

    public static Object removeStore(UUID uuid, String key) {
        Map<String, Object> data = storeData.get(uuid);
        if (data != null) {
            return data.remove(key);
        }
        return null;
    }

    public static void clearStore(UUID uuid) {
        storeData.remove(uuid);
    }

    public static void readStore(UUID uuid, FileNode node) {
        storeData.put(uuid, node.toTypeMap());
    }

    public static void writeStore(UUID uuid, FileNode node) {
        Map<String, Object> map = storeData.get(uuid);
        if (map != null) {
            node.fromTypeMap(map);
        }
    }
}
