package org.soraworld.violet.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * The type Reflects.
 *
 * @author Himmelt
 */
public class Reflects {
    /**
     * Gets class.
     *
     * @param names the names
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     */
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

    /**
     * Gets declared field.
     *
     * @param clazz the clazz
     * @param names the names
     * @return the declared field
     * @throws NoSuchFieldException the no such field exception
     */
    public static Field getDeclaredField(@NotNull Class<?> clazz, String... names) throws NoSuchFieldException {
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


    /**
     * 根据名称获取无参数的方法.
     *
     * @param clazz the clazz
     * @param names the names
     * @return the method
     * @throws NoSuchMethodException the no such method exception
     */
    public static Method getMethod(@NotNull Class<?> clazz, String... names) throws NoSuchMethodException {
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
