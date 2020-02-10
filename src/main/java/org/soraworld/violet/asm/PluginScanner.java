package org.soraworld.violet.asm;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * @author Himmelt
 */
public final class PluginScanner {

    private static final Pattern CLASS_FILE = Pattern.compile("[^\\s$]+(\\$[^\\s]+)?\\.class$");

    private PluginScanner() {
    }

    @NotNull
    public static Set<ClassInfo> scan(@NotNull File jarFile) {
        return scan(jarFile, null);
    }

    @NotNull
    public static Set<ClassInfo> scan(@NotNull File jarFile, Predicate<ClassInfo> filter) {
        HashSet<ClassInfo> set = new HashSet<>();
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> et = jar.entries();
            while (et.hasMoreElements()) {
                JarEntry entry = et.nextElement();
                String name = entry.getName();
                if (entry.isDirectory() || name == null || name.isEmpty() || name.startsWith("__MACOSX") || !CLASS_FILE.matcher(name).matches()) {
                    continue;
                }
                try (InputStream stream = jar.getInputStream(entry)) {
                    ClassReader reader = new ClassReader(stream);
                    VioletVisitor visitor = new VioletVisitor();
                    reader.accept(visitor, ClassReader.SKIP_CODE);
                    ClassInfo clazz = visitor.getInfo();
                    if (filter == null || filter.test(clazz)) {
                        set.add(clazz);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.unmodifiableSet(set);
    }
}
