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
    public static Set<Class<?>> scanClasses(File jarFile, String packageName, ClassLoader loader) {
        Set<Class<?>> classes = new HashSet<>();
        try {
            JarFile jar = new JarFile(jarFile);
            Enumeration<JarEntry> et = jar.entries();
            while (et.hasMoreElements()) {
                try {
                    JarEntry entry = et.nextElement();
                    String name = entry.getName().replace("/", ".");
                    if (name.startsWith(packageName) && name.endsWith(".class")) {
                        classes.add(Class.forName(name.substring(0, name.length() - 6), false, loader));
                    }
                } catch (Throwable e) {
                    System.out.println("!!!!! Package Classes scan Error: " + e.getLocalizedMessage());
                }
            }
            jar.close();
        } catch (Throwable e) {
            System.out.println("!!!!! Package Classes scan Error: " + e.getLocalizedMessage());
        }
        return classes;
    }
}
