package org.soraworld.violet.version;

import org.jetbrains.annotations.NotNull;

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
    private final boolean bukkit;
    private final boolean sponge;

    public McVersion(byte major, byte minor, byte patch, byte craft) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.craft = craft;
        this.value = craft & 0xff | (patch & 0xff) << 8 | (minor & 0xff) << 16 | (major & 0xff) << 24;
        this.bukkit = false;
        this.sponge = false;
    }

    private static final Pattern VERSION_ALONE = Pattern.compile("(\\d+\\.){1,3}\\d+");
    private static final Pattern VERSION_RANGE = Pattern.compile("[\\[(](\\d+\\.){1,3}\\d+,(\\d+\\.){1,3}\\d+[])]");

    public McVersion(byte major, byte minor, byte patch, byte craft, boolean bukkit, boolean sponge) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.craft = craft;
        this.value = craft & 0xff | (patch & 0xff) << 8 | (minor & 0xff) << 16 | (major & 0xff) << 24;
        this.bukkit = bukkit;
        this.sponge = sponge;
    }

    public McVersion(int major, int minor, int patch, int craft, boolean bukkit, boolean sponge) {
        this((byte) major, (byte) minor, (byte) patch, (byte) craft, bukkit, sponge);
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

    public boolean isBukkit() {
        return bukkit;
    }

    public boolean isSponge() {
        return sponge;
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

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof McVersion && value == ((McVersion) obj).value;
    }

    private static McVersion parse(@NotNull String text) {
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
        return new McVersion(major, minor, patch, craft);
    }
}
