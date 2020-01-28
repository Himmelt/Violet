package org.soraworld.violet.inject;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Himmelt
 */
public final class Injector {

    private final List<Object> values = new ArrayList<>();

    public void addValue(Object value) {
        if (value != null) {
            values.add(value);
        }
    }

    public void addValues(Object... values) {
        if (values != null) {
            Collections.addAll(this.values, values);
        }
    }

    public void inject(@NotNull Object instance) {
        if (instance instanceof Class<?>) {
            inject((Class<?>) instance);
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

    public void inject(@NotNull Class<?> clazz) {
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
