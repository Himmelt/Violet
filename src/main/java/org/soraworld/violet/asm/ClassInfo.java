package org.soraworld.violet.asm;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;

/**
 * @author Himmelt
 */
public final class ClassInfo {

    private final String name;
    private final int access;
    private final int version;
    private final Set<String> annotations;

    public ClassInfo(@NotNull String name, int access, int version, @NotNull Set<String> annotations) {
        this.name = name;
        this.access = access;
        this.version = version;
        this.annotations = annotations;
    }

    public String getName() {
        return name;
    }

    public boolean hasAnnotation(@NotNull Class<?> annotation) {
        return annotations.contains(annotation.getName());
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
        return "{name:" + name + ",version:" + version + ",annotations:" + Arrays.toString(annotations.toArray()) + "}";
    }
}
