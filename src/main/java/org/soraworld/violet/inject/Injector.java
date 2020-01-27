package org.soraworld.violet.inject;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author Himmelt
 */
public final class Injector {
    public static void inject(@NotNull Object instance, Object... values) {
        if (instance instanceof Class<?>) {
            inject((Class<?>) instance, values);
        } else {
            Field[] fields = instance.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                Inject inject = field.getAnnotation(Inject.class);
                if (inject != null) {
                    field.setAccessible(true);
                    Class<?> type = field.getType();
                    for (Object value : values) {
                        if (type.isAssignableFrom(value.getClass())) {
                            try {
                                field.set(instance, value);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public static void inject(@NotNull Class<?> clazz, Object... values) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                field.setAccessible(true);
                Class<?> type = field.getType();
                for (Object value : values) {
                    if (type.isAssignableFrom(value.getClass())) {
                        try {
                            field.set(null, value);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
