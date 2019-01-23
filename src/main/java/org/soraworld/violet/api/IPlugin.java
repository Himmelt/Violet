package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.hocon.node.Paths;
import org.soraworld.violet.command.BaseSubCmds;
import org.soraworld.violet.exception.MainManagerException;
import org.soraworld.violet.inject.*;
import org.soraworld.violet.util.ClassUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 插件接口.
 *
 * @param <M> 主管理器类型
 */
public interface IPlugin<M extends IManager> {

    /**
     * 扫描 jar 内与插件主类同包的所有类.
     * 此默认方法必须在实现类的构造方法中执行.
     */
    default void scanJarPackageClasses() {
        File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
        PluginData pluginData = getPluginData();
        for (Class<?> clazz : ClassUtils.getClasses(jarFile, getClass().getPackage().getName())) {
            if (clazz.getAnnotation(Command.class) != null) pluginData.commandClasses.add(clazz);
            if (clazz.getAnnotation(EventListener.class) != null) pluginData.listenerClasses.add(clazz);
            if (clazz.getAnnotation(Inject.class) != null) pluginData.injectClasses.add(clazz);
            if (clazz.getAnnotation(MainManager.class) != null) pluginData.mainManagerClass = clazz;
        }
    }

    /**
     * 获取插件名称.
     * 推荐不要带空格和特殊字符.
     *
     * @return 名称 name
     */
    String getName();

    /**
     * 获取插件id.
     * 推荐全部使用小写字母和数字，且务必不要带空格和特殊字符.
     *
     * @return id id
     */
    default String getId() {
        return getName().toLowerCase().replace(' ', '_');
    }

    /**
     * 获取资源id.
     * 推荐全部使用小写字母和数字，且务必不要带空格和特殊字符.
     * 特别注意: 资源文件夹 assets 的子目录的名字要和该 id 保持一致，
     * 否则将无法提取资源文件 !!!
     *
     * @return 资源id string
     */
    default String assetsId() {
        return getId();
    }

    /**
     * 获取插件版本.
     * 请使用 x.y.z 格式
     *
     * @return 版本 version
     */
    String getVersion();

    /**
     * On load.
     *
     * @throws MainManagerException the main manager exception
     */
    default void onLoad() throws MainManagerException {
        Path path = getRootPath();
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        injectMainManager(path);
    }

    /**
     * Gets root path.
     *
     * @return the root path
     */
    Path getRootPath();

    /**
     * 插件是否已启用.
     *
     * @return 是否启用 boolean
     */
    boolean isEnabled();

    /**
     * On enable.
     */
    default void onEnable() {
        IManager manager = getManager();
        manager.beforeLoad();
        manager.load();
        manager.afterLoad();
        injectCommands();
        registerCommands();
        disableCommands();
        injectListeners();
        registerListeners();
        manager.consoleKey("pluginEnabled", getId() + "-" + getVersion());
        afterEnable();
    }

    /**
     * 插件启用后.
     */
    default void afterEnable() {
    }

    /**
     * 插件停用前.
     */
    default void beforeDisable() {
    }

    /**
     * On disable.
     */
    default void onDisable() {
        beforeDisable();
        IManager manager = getManager();
        if (manager != null) {
            manager.consoleKey("pluginDisabled", getId() + "-" + getVersion());
            if (manager.canSaveOnDisable()) {
                manager.consoleKey(manager.save() ? "configSaved" : "configSaveFailed");
            }
        }
    }

    /**
     * Gets manager.
     *
     * @return the manager
     */
    M getManager();

    /**
     * Sets manager.
     *
     * @param manager the manager
     */
    void setManager(M manager);

    /**
     * 从jar获取资源文件的 {@link InputStream}.
     *
     * @param path assets目录下，插件id之后的路径
     * @return 资源文件的输入流 asset stream
     */
    default InputStream getAssetStream(String path) {
        return getClass().getResourceAsStream("/assets/" + assetsId() + '/' + path);
    }

    /**
     * 从jar获取资源文件的 {@link URL}.
     *
     * @param path assets目录下，插件id之后的路径
     * @return 资源文件的 URL
     */
    default URL getAssetURL(String path) {
        return getClass().getResource("/assets/" + assetsId() + '/' + path);
    }

    /**
     * Inject listeners.
     */
    default void injectListeners() {
        for (Class<?> clazz : getPluginData().listenerClasses) {
            EventListener listener = clazz.getAnnotation(EventListener.class);
            if (listener != null) {
                try {
                    Object instance = clazz.getConstructor().newInstance();
                    injectIntoInstance(instance);
                    registerListener(instance);
                } catch (Throwable e) {
                    getManager().console("CANT construct instance for " + clazz.getName() + " .");
                }
            }
        }
    }

    /**
     * Register listener.
     *
     * @param listener the listener
     */
    void registerListener(@NotNull Object listener);

    /**
     * Register listeners.
     */
    default void registerListeners() {
    }

    /**
     * Inject commands.
     */
    default void injectCommands() {
        for (Class<?> clazz : getPluginData().commandClasses) {
            Command annotation = clazz.getAnnotation(Command.class);
            if (annotation != null) {
                try {
                    Object instance = clazz.getConstructor().newInstance();
                    injectIntoInstance(instance);
                    ICommand command = registerCommand(annotation);
                    if (command != null) {
                        command.extractSub(instance);
                        if (command.getName().equalsIgnoreCase(getId())) {
                            command.extractSub(new BaseSubCmds(getManager()));
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Register command command.
     *
     * @param annotation the clazz
     * @return the command
     */
    @Nullable
    ICommand registerCommand(@NotNull Command annotation);

    /**
     * Register command boolean.
     *
     * @param command the command
     * @return the boolean
     */
    boolean registerCommand(@NotNull ICommand command);

    /**
     * Register commands.
     */
    default void registerCommands() {
    }

    /**
     * Gets plugin data.
     *
     * @return the plugin data
     */
    PluginData getPluginData();

    /**
     * Disable commands.
     */
    default void disableCommands() {
        for (ICommand command : getPluginData().commands) {
            for (String name : getManager().getDisableCmds(command.getName())) {
                command.removeSub(new Paths(name));
            }
        }
    }

    /**
     * Inject main manager.
     *
     * @param path the path
     * @throws MainManagerException the main manager exception
     */
    default void injectMainManager(@NotNull Path path) throws MainManagerException {
        M manager = registerManager(path);
        if (manager == null) {
            Class<?> clazz = getPluginData().mainManagerClass;
            if (clazz != null && IManager.class.isAssignableFrom(clazz)) {
                Constructor[] constructors = clazz.getConstructors();
                for (Constructor constructor : constructors) {
                    Class<?>[] params = constructor.getParameterTypes();
                    if (params.length == 2 && IPlugin.class.isAssignableFrom(params[0]) && Path.class.equals(params[1])) {
                        constructor.setAccessible(true);
                        try {
                            manager = (M) constructor.newInstance(this, path);
                            break;
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if (manager == null) throw new MainManagerException("CANT register or inject main manager !!!");
        setManager(manager);
        getPluginData().injectClasses.forEach(this::injectIntoStatic);
    }

    /**
     * Inject into static.
     *
     * @param clazz the clazz
     */
    default void injectIntoStatic(@NotNull Class<?> clazz) {
        IManager manager = getManager();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) continue;
            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                field.setAccessible(true);
                if (field.getType().isAssignableFrom(manager.getClass())) {
                    try {
                        field.set(null, manager);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else if (field.getType().isAssignableFrom(getClass())) {
                    try {
                        field.set(null, this);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Inject into instance.
     *
     * @param instance the instance
     */
    default void injectIntoInstance(@NotNull Object instance) {
        IManager manager = getManager();
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                field.setAccessible(true);
                if (field.getType().isAssignableFrom(manager.getClass())) {
                    try {
                        field.set(instance, manager);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else if (field.getType().isAssignableFrom(getClass())) {
                    try {
                        field.set(instance, this);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Register manager m.
     *
     * @param path the path
     * @return the m
     */
    @Nullable
    default M registerManager(@NotNull Path path) {
        return null;
    }
}
