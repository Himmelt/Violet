package org.soraworld.violet.version;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * @author Himmelt
 */
public final class McVersion {
    private final byte major;
    private final byte minor;
    private final byte patch;
    private final byte craft;
    private final int value;

    // TODO correct all versions' craft version
    public static final McVersion UNKNOWN = new McVersion(0, 0, 0, 0);
    public static final McVersion v1_7_10 = new McVersion(1, 7, 10, 4);
    public static final McVersion v1_8 = new McVersion(1, 8, 0, 1);
    public static final McVersion v1_8_1 = new McVersion(1, 8, 1, 1);
    public static final McVersion v1_8_2 = new McVersion(1, 8, 2, 1);
    public static final McVersion v1_8_3 = new McVersion(1, 8, 3, 2);
    public static final McVersion v1_8_4 = new McVersion(1, 8, 4, 1);
    public static final McVersion v1_8_5 = new McVersion(1, 8, 5, 1);
    public static final McVersion v1_8_6 = new McVersion(1, 8, 6, 1);
    public static final McVersion v1_8_7 = new McVersion(1, 8, 7, 1);
    public static final McVersion v1_8_8 = new McVersion(1, 8, 8, 1);
    public static final McVersion v1_8_9 = new McVersion(1, 8, 9, 1);
    public static final McVersion v1_9 = new McVersion(1, 9, 0, 1);
    public static final McVersion v1_9_1 = new McVersion(1, 9, 1, 1);
    public static final McVersion v1_9_2 = new McVersion(1, 9, 2, 1);
    public static final McVersion v1_9_3 = new McVersion(1, 9, 3, 1);
    public static final McVersion v1_9_4 = new McVersion(1, 9, 4, 1);
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
    public static final McVersion v1_13_1 = new McVersion(1, 13, 1, 1);
    public static final McVersion v1_13_2 = new McVersion(1, 13, 2, 1);
    public static final McVersion v1_14 = new McVersion(1, 14, 0, 1);
    public static final McVersion v1_14_1 = new McVersion(1, 14, 1, 1);
    public static final McVersion v1_14_2 = new McVersion(1, 14, 2, 1);
    public static final McVersion v1_14_3 = new McVersion(1, 14, 3, 1);
    public static final McVersion v1_14_4 = new McVersion(1, 14, 4, 1);
    public static final McVersion v1_15 = new McVersion(1, 15, 0, 1);
    public static final McVersion v1_15_1 = new McVersion(1, 15, 1, 1);
    public static final McVersion v1_15_2 = new McVersion(1, 15, 2, 1);

    private static final HashMap<Integer, McVersion> MC_VERSIONS = new HashMap<>();
    private static final Pattern VERSION_ALONE = Pattern.compile("(\\d+\\.){1,3}\\d+");
    private static final Pattern VERSION_RANGE = Pattern.compile("[\\[(](\\d+\\.){1,3}\\d+,(\\d+\\.){1,3}\\d+[])]");

    static {
        MC_VERSIONS.put(UNKNOWN.value, UNKNOWN);
        MC_VERSIONS.put(v1_7_10.value, v1_7_10);
        MC_VERSIONS.put(v1_8.value, v1_8);
        MC_VERSIONS.put(v1_8_1.value, v1_8_1);
        MC_VERSIONS.put(v1_8_2.value, v1_8_2);
        MC_VERSIONS.put(v1_8_3.value, v1_8_3);
        MC_VERSIONS.put(v1_8_4.value, v1_8_4);
        MC_VERSIONS.put(v1_8_5.value, v1_8_5);
        MC_VERSIONS.put(v1_8_6.value, v1_8_6);
        MC_VERSIONS.put(v1_8_7.value, v1_8_7);
        MC_VERSIONS.put(v1_8_8.value, v1_8_8);
        MC_VERSIONS.put(v1_8_9.value, v1_8_9);
        MC_VERSIONS.put(v1_9.value, v1_9);
        MC_VERSIONS.put(v1_9_1.value, v1_9_1);
        MC_VERSIONS.put(v1_9_2.value, v1_9_2);
        MC_VERSIONS.put(v1_9_3.value, v1_9_3);
        MC_VERSIONS.put(v1_9_4.value, v1_9_4);
        MC_VERSIONS.put(v1_10.value, v1_10);
        MC_VERSIONS.put(v1_10_1.value, v1_10_1);
        MC_VERSIONS.put(v1_10_2.value, v1_10_2);
        MC_VERSIONS.put(v1_11.value, v1_11);
        MC_VERSIONS.put(v1_11_1.value, v1_11_1);
        MC_VERSIONS.put(v1_11_2.value, v1_11_2);
        MC_VERSIONS.put(v1_12.value, v1_12);
        MC_VERSIONS.put(v1_12_1.value, v1_12_1);
        MC_VERSIONS.put(v1_12_2.value, v1_12_2);
        MC_VERSIONS.put(v1_13.value, v1_13);
        MC_VERSIONS.put(v1_13_1.value, v1_13_1);
        MC_VERSIONS.put(v1_13_2.value, v1_13_2);
        MC_VERSIONS.put(v1_14.value, v1_14);
        MC_VERSIONS.put(v1_14_1.value, v1_14_1);
        MC_VERSIONS.put(v1_14_2.value, v1_14_2);
        MC_VERSIONS.put(v1_14_3.value, v1_14_3);
        MC_VERSIONS.put(v1_14_4.value, v1_14_4);
        MC_VERSIONS.put(v1_15.value, v1_15);
        MC_VERSIONS.put(v1_15_1.value, v1_15_1);
        MC_VERSIONS.put(v1_15_2.value, v1_15_2);
    }

    public McVersion(int major, int minor, int patch, int craft) {
        this.major = (byte) major;
        this.minor = (byte) minor;
        this.patch = (byte) patch;
        this.craft = (byte) craft;
        this.value = this.patch & 0xff | (this.minor & 0xff) << 8 | (this.major & 0xff) << 16;
    }

    public int value() {
        return value;
    }

    public int major() {
        return major;
    }

    public int minor() {
        return minor;
    }

    public int patch() {
        return patch;
    }

    public int craft() {
        return craft;
    }

    public boolean match(int major) {
        return this.major == major;
    }

    public boolean match(int major, int minor) {
        return this.major == major && this.minor == minor;
    }

    public boolean match(int major, int minor, int patch) {
        return this.major == major && this.minor == minor && this.patch == patch;
    }

    public boolean match(@NotNull McVersion version) {
        return this.major == version.major && this.minor == version.minor && this.patch == version.patch;
    }

    public boolean matchCraft(int major, int minor, int craft) {
        return this.major == major && this.minor == minor && this.craft == craft;
    }

    public boolean match(@NotNull String version) {
        if (VERSION_ALONE.matcher(version).matches()) {
            return this.value == parse(version).value;
        } else if (VERSION_RANGE.matcher(version).matches()) {
            String[] ss = version.split(",");
            McVersion left = parse(ss[0].substring(1));
            McVersion right = parse(ss[1].substring(0, ss[1].length() - 1));
            if (ss[0].charAt(0) == '[') {
                if (ss[1].charAt(ss[1].length() - 1) == ']') {
                    return value >= left.value && value <= right.value;
                } else {
                    return value >= left.value && value < right.value;
                }
            } else {
                if (ss[1].charAt(ss[1].length() - 1) == ']') {
                    return value > left.value && value <= right.value;
                } else {
                    return value > left.value && value < right.value;
                }
            }
        }
        return false;
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

    public boolean higher(@NotNull McVersion version) {
        return this.value > version.value;
    }

    public boolean higherEquals(@NotNull McVersion version) {
        return this.value >= version.value;
    }

    public boolean lower(@NotNull McVersion version) {
        return this.value < version.value;
    }

    public boolean lowerEquals(@NotNull McVersion version) {
        return this.value <= version.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof McVersion && value == ((McVersion) obj).value;
    }

    @Override
    public String toString() {
        return "" + major + "." + minor + "." + patch;
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
