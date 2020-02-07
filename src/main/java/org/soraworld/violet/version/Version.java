package org.soraworld.violet.version;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * @author Himmelt
 */
public class Version {
    final byte major;
    final byte minor;
    final byte patch;
    final int value;

    private static final Pattern VERSION_ALONE = Pattern.compile("(\\d+\\.){1,3}\\d+");
    private static final Pattern VERSION_RANGE = Pattern.compile("[\\[(](\\d+\\.){1,3}\\d+,(\\d+\\.){1,3}\\d+[])]");

    public Version(int major, int minor, int patch) {
        this.major = (byte) major;
        this.minor = (byte) minor;
        this.patch = (byte) patch;
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

    public boolean match(int major) {
        return this.major == major;
    }

    public boolean match(int major, int minor) {
        return this.major == major && this.minor == minor;
    }

    public boolean match(int major, int minor, int patch) {
        return this.major == major && this.minor == minor && this.patch == patch;
    }

    public boolean match(@NotNull Version version) {
        return this.major == version.major && this.minor == version.minor && this.patch == version.patch;
    }

    public boolean match(@NotNull String version) {
        if (VERSION_ALONE.matcher(version).matches()) {
            return this.value == parse(version).value;
        } else if (VERSION_RANGE.matcher(version).matches()) {
            String[] ss = version.split(",");
            Version left = parse(ss[0].substring(1));
            Version right = parse(ss[1].substring(0, ss[1].length() - 1));
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

    public boolean compatible(@NotNull Version version) {
        return this.major == version.major && this.minor == version.minor;
    }

    public boolean higher(int major, int minor, int patch) {
        return this.major > major || this.minor > minor || this.patch > patch;
    }

    public boolean higherEquals(int major, int minor, int patch) {
        return this.major > major || this.major == major && this.minor > minor ||
                this.minor == minor && this.patch > patch;
    }

    public boolean lower(int major, int minor, int patch) {
        return this.major < major || this.minor < minor || this.patch < patch;
    }

    public boolean lowerEquals(int major, int minor, int patch) {
        return this.major < major || this.major == major && this.minor < minor ||
                this.minor == minor && this.patch < patch;
    }

    public boolean higher(@NotNull Version version) {
        return this.value > version.value;
    }

    public boolean higherEquals(@NotNull Version version) {
        return this.value >= version.value;
    }

    public boolean lower(@NotNull Version version) {
        return this.value < version.value;
    }

    public boolean lowerEquals(@NotNull Version version) {
        return this.value <= version.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Version && value == ((Version) obj).value;
    }

    @Override
    public String toString() {
        return "" + major + "." + minor + "." + patch;
    }

    public static Version parse(@NotNull String text) {
        String[] ss = text.split("\\.");
        int length = ss.length;
        byte major = 0, minor = 0, patch = 0;
        if (length >= 1) {
            major = Byte.parseByte(ss[0]);
        }
        if (length >= 2) {
            minor = Byte.parseByte(ss[1]);
        }
        if (length >= 3) {
            patch = Byte.parseByte(ss[2]);
        }
        return new Version(major, minor, patch);
    }
}
