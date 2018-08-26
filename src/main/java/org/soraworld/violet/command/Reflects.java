package org.soraworld.violet.command;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Reflects {
    private static final ConcurrentHashMap<Class<?>, ArrayList<Method>> CLASS_METHODS = new ConcurrentHashMap<>();

    public static ArrayList<Method> getMethods(@Nonnull Class<?> clazz) {
        ArrayList<Method> list = CLASS_METHODS.get(clazz);
        if (list == null) {
            list = new ArrayList<>();
            Method[] methods = clazz.getDeclaredMethods();
            if (methods != null && methods.length > 0) {
                for (Method method : methods) {
                    Sub sub = method.getAnnotation(Sub.class);
                    if (sub == null) continue;
                    Class<?> ret = method.getReturnType();
                    if (!ret.equals(Void.class) && !ret.equals(void.class)) continue;
                    Class<?>[] params = method.getParameterTypes();
                    // TODO Hint check params[0] when get Executor && set method access true ??? needed ?
                    if (params.length == 2 && params[1] == CommandArgs.class) list.add(method);
                }
            }
            CLASS_METHODS.put(clazz, list);
        }
        return list;
    }
}
