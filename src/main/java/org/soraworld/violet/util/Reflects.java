package org.soraworld.violet.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Himmelt
 */
public class Reflects {
    public static Class<?> getClass(String... names) throws ClassNotFoundException {
        if (names == null || names.length == 0) {
            throw new ClassNotFoundException("empty class name");
        }
        for (String name : names) {
            try {
                return Class.forName(name);
            } catch (Throwable ignored) {
            }
        }
        throw new ClassNotFoundException(Arrays.toString(names));
    }

    // TODO optimize
    public static Field getFiled(Class<?> clazz, String... names) throws NoSuchFieldException {
        if (names == null || names.length == 0) {
            throw new NoSuchFieldException("empty field name");
        }
        for (String name : names) {
            try {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (Throwable ignored) {
            }
        }
        throw new NoSuchFieldException(Arrays.toString(names));
    }

    // TODO optimize
    public static Method getMethod(Class<?> clazz, String... names) throws NoSuchMethodException {
        if (names == null || names.length == 0) {
            throw new NoSuchMethodException("empty method name");
        }
        for (String name : names) {
            try {
                Method method = clazz.getMethod(name);
                method.setAccessible(true);
                return method;
            } catch (Throwable ignored) {
            }
        }
        throw new NoSuchMethodException(Arrays.toString(names));
    }
}
