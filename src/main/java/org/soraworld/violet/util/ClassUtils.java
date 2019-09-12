package org.soraworld.violet.util;

import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Himmelt
 */
public final class ClassUtils {
    public static Set<Class<?>> getClasses(File jarFile, String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        try {
            JarFile file = new JarFile(jarFile);
            for (Enumeration<JarEntry> entry = file.entries(); entry.hasMoreElements(); ) {
                JarEntry jarEntry = entry.nextElement();
                String name = jarEntry.getName().replace("/", ".");
                if (name.startsWith(packageName) && name.endsWith(".class")) {
                    classes.add(Class.forName(name.substring(0, name.length() - 6)));
                }
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }
}
