package org.soraworld.violet.asm;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.inject.McVer;
import org.soraworld.violet.version.McVersion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Himmelt
 */
public final class ClassInfo {

    private final String name;
    private final int access;
    private final int version;
    private final HashMap<String, HashMap<String, Object>> annotations;

    public ClassInfo(@NotNull String name, int access, int version, @NotNull HashMap<String, HashMap<String, Object>> annotations) {
        this.name = name;
        this.access = access;
        this.version = version;
        this.annotations = annotations;
    }

    public String getName() {
        return name;
    }

    public boolean hasAnnotation(@NotNull Class<?> annotation) {
        return annotations.containsKey(annotation.getName());
    }

    public boolean matchMcVersion(@NotNull McVersion version) {
        Map<String, Object> map = annotations.get(McVer.class.getName());
        return map == null || version.match(String.valueOf(map.getOrDefault("value", "")));
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClassInfo && name.equals(((ClassInfo) obj).name);
    }

    @Override
    public String toString() {
        return "{name:" + name + ",version:" + version + ",annotations:" + Arrays.toString(annotations.keySet().toArray()) + "}";
    }
}
