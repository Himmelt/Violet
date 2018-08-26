package org.soraworld.violet.command;

import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Node;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Reflects {

    private static final ArrayList<Class> traversed = new ArrayList<>();
    private static final ConcurrentHashMap<Class, ArrayList<Method>> CLASS_METHODS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class, ArrayList<Method>> STATIC_METHODS = new ConcurrentHashMap<>();

    /* 注册的 参数里可以加上方法名，根据方法名查找更快*/
    private static void traverse(Class clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        if (methods != null && methods.length > 0) {
            ArrayList<Method> clazzList = new ArrayList<>();
            ArrayList<Method> staticList = new ArrayList<>();
            for (Method method : methods) {
                Sub cmd = method.getAnnotation(Sub.class);
                if (cmd == null) continue;
                Class<?> ret = method.getReturnType();
                if (!ret.equals(Void.class) && !ret.equals(void.class)) continue;
                Class[] params = method.getParameterTypes();
                // TODO Hint check params[0] when get Executor && set method access true ??? needed ?
                if (params.length == 2 && params[1] == CommandArgs.class) {
                    int modify = method.getModifiers();
                    if (Modifier.isAbstract(modify)) continue;
                    if (Modifier.isStatic(modify)) staticList.add(method);
                    else clazzList.add(method);
                }
            }
            CLASS_METHODS.put(clazz, clazzList);
            STATIC_METHODS.put(clazz, staticList);
        }
        traversed.add(clazz);
    }

    public static ArrayList<Method> getMethods(Class<?> clazz) {
        if (clazz == null || clazz == Object.class) return new ArrayList<>();
        if (traversed.contains(clazz)) {
            ArrayList<Method> list = CLASS_METHODS.get(clazz);
            return list == null ? new ArrayList<>() : list;
        }
        traverse(clazz);
        ArrayList<Method> list = CLASS_METHODS.get(clazz);
        return list == null ? new ArrayList<>() : list;
    }

    /* upper 不能是interface */
    public static <T> ArrayList<Method> getMethods(Class<T> lower, Class<? super T> upper) {
        ArrayList<Method> list = getMethods(lower);
        if (upper.isInterface()) return list;
        Class<?> parent = lower.getSuperclass();
        while (parent != null && parent != Object.class && parent != upper) {
            list.addAll(getMethods(parent));
            parent = parent.getSuperclass();
        }
        list.addAll(getMethods(upper));
        return list;
    }

    public static ArrayList<Method> getStaticMethods(Class<?> clazz) {
        if (clazz == null || clazz == Object.class) return new ArrayList<>();
        if (traversed.contains(clazz)) {
            ArrayList<Method> list = STATIC_METHODS.get(clazz);
            return list == null ? new ArrayList<>() : list;
        }
        traverse(clazz);
        ArrayList<Method> list = STATIC_METHODS.get(clazz);
        return list == null ? new ArrayList<>() : list;
    }

    /* upper 不能是interface */
    public static <T> ArrayList<Method> getStaticMethods(Class<T> lower, Class<? super T> upper) {
        ArrayList<Method> list = getStaticMethods(lower);
        if (upper.isInterface()) return list;
        Class<?> parent = lower.getSuperclass();
        while (parent != null && parent != Object.class && parent != upper) {
            list.addAll(getStaticMethods(parent));
            parent = parent.getSuperclass();
        }
        list.addAll(getStaticMethods(upper));
        return list;
    }


    public void test() {
        getMethods(FileNode.class, Node.class);
    }

}
