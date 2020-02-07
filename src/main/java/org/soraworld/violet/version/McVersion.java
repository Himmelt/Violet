package org.soraworld.violet.version;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * @author Himmelt
 */
public final class McVersion extends Version {
    private final byte craft;

    public static final McVersion UNKNOWN = new McVersion(0, 0, 0, 0);
    public static final McVersion v1_7_10 = new McVersion(1, 7, 10, 4);
    public static final McVersion v1_8 = new McVersion(1, 8, 0, 1);
    public static final McVersion v1_8_1 = new McVersion(1, 8, 1, 1);
    public static final McVersion v1_8_2 = new McVersion(1, 8, 2, 1);
    public static final McVersion v1_8_3 = new McVersion(1, 8, 3, 2);
    public static final McVersion v1_8_4 = new McVersion(1, 8, 4, 3);
    public static final McVersion v1_8_5 = new McVersion(1, 8, 5, 3);
    public static final McVersion v1_8_6 = new McVersion(1, 8, 6, 3);
    public static final McVersion v1_8_7 = new McVersion(1, 8, 7, 3);
    public static final McVersion v1_8_8 = new McVersion(1, 8, 8, 3);
    public static final McVersion v1_8_9 = new McVersion(1, 8, 9, 3);
    public static final McVersion v1_9 = new McVersion(1, 9, 0, 1);
    public static final McVersion v1_9_1 = new McVersion(1, 9, 1, 1);
    public static final McVersion v1_9_2 = new McVersion(1, 9, 2, 1);
    public static final McVersion v1_9_3 = new McVersion(1, 9, 3, 2);
    public static final McVersion v1_9_4 = new McVersion(1, 9, 4, 2);
    public static final McVersion v1_10 = new McVersion(1, 10, 0, 1);
    public static final McVersion v1_10_1 = new McVersion(1, 10, 1, 1);
    public static final McVersion v1_10_2 = new McVersion(1, 10, 2, 1);
    public static final McVersion v1_11 = new McVersion(1, 11, 0, 1);
    public static final McVersion v1_11_1 = new McVersion(1, 11, 1, 1);
    public static final McVersion v1_11_2 = new McVersion(1, 11, 2, 1);
    public static final McVersion v1_12 = new McVersion(1, 12, 0, 1);
    public static final McVersion v1_12_1 = new McVersion(1, 12, 1, 1);
    public static final McVersion v1_12_2 = new McVersion(1, 12, 2, 1);
    public static final McVersion v1_13 = new McVersion(1, 13, 0, 1);
    public static final McVersion v1_13_1 = new McVersion(1, 13, 1, 2);
    public static final McVersion v1_13_2 = new McVersion(1, 13, 2, 2);
    public static final McVersion v1_14 = new McVersion(1, 14, 0, 1);
    public static final McVersion v1_14_1 = new McVersion(1, 14, 1, 1);
    public static final McVersion v1_14_2 = new McVersion(1, 14, 2, 1);
    public static final McVersion v1_14_3 = new McVersion(1, 14, 3, 1);
    public static final McVersion v1_14_4 = new McVersion(1, 14, 4, 1);
    public static final McVersion v1_15 = new McVersion(1, 15, 0, 1);
    public static final McVersion v1_15_1 = new McVersion(1, 15, 1, 1);
    public static final McVersion v1_15_2 = new McVersion(1, 15, 2, 1);
    public static final McVersion v1_16 = new McVersion(1, 16, 0, 1);
    public static final McVersion v1_16_1 = new McVersion(1, 16, 1, 1);
    public static final McVersion v1_16_2 = new McVersion(1, 16, 2, 1);

    private static final HashMap<Integer, McVersion> MC_VERSIONS = new HashMap<>();

    static {
        try {
            Field[] fields = McVersion.class.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == McVersion.class && field.getName().matches("v\\d(_\\d+){1,2}")) {
                    McVersion version = (McVersion) field.get(McVersion.class);
                    MC_VERSIONS.put(version.value, version);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public McVersion(int major, int minor, int patch, int craft) {
        super(major, minor, patch);
        this.craft = (byte) craft;
    }

    public int craft() {
        return craft;
    }

    public boolean matchCraft(int major, int minor, int craft) {
        return this.major == major && this.minor == minor && this.craft == craft;
    }

    public boolean higher(int major, int minor, int patch, int craft) {
        return this.major > major || this.minor > minor || this.patch > patch || this.craft > craft;
    }

    public boolean higherEquals(int major, int minor, int patch, int craft) {
        return this.major > major || this.major == major && this.minor > minor ||
                this.minor == minor && this.patch > patch || this.patch == patch && this.craft >= craft;
    }

    public boolean lower(int major, int minor, int patch, int craft) {
        return this.major < major || this.minor < minor || this.patch < patch || this.craft < craft;
    }

    public boolean lowerEquals(int major, int minor, int patch, int craft) {
        return this.major < major || this.major == major && this.minor < minor ||
                this.minor == minor && this.patch < patch || this.patch == patch && this.craft <= craft;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof McVersion && value == ((McVersion) obj).value;
    }

    public static McVersion parse(@NotNull String text) {
        String[] ss = text.split("\\.");
        int length = ss.length;
        byte major = 0, minor = 0, patch = 0, craft = 0;
        if (length >= 1) {
            major = Byte.parseByte(ss[0]);
        }
        if (length >= 2) {
            minor = Byte.parseByte(ss[1]);
        }
        if (length >= 3) {
            patch = Byte.parseByte(ss[2]);
        }
        if (length >= 4) {
            craft = Byte.parseByte(ss[3]);
        }
        McVersion version = new McVersion(major, minor, patch, craft);
        return MC_VERSIONS.computeIfAbsent(version.value, val -> version);
    }
}
