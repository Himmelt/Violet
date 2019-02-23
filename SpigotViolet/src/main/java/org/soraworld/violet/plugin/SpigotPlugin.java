package org.soraworld.violet.plugin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.command.BaseSubCmds;
import org.soraworld.violet.command.VCommand;
import org.soraworld.violet.inject.Command;
import org.soraworld.violet.inject.EventListener;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.inject.MainManager;
import org.soraworld.violet.manager.IManager;
import org.soraworld.violet.manager.VManager;
import org.soraworld.violet.util.ChatColor;
import org.soraworld.violet.util.ClassUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

public class SpigotPlugin<M extends VManager> extends JavaPlugin implements IPlugin<M> {

    protected M manager;
    protected Class<?> mainManagerClass;
    protected final HashSet<Class<?>> injectClasses = new HashSet<>();
    protected final HashSet<Class<?>> commandClasses = new HashSet<>();
    protected final HashSet<Class<?>> listenerClasses = new HashSet<>();
    private static final CommandMap commandMap;

    static {
        CommandMap map = null;
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            Object object = field.get(Bukkit.getServer());
            if (object instanceof CommandMap) {
                map = (CommandMap) object;
            } else System.out.println("Invalid CommandMap in Server !!!");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        commandMap = map;
    }

    {
        scanJarPackageClasses();
    }

    private void scanJarPackageClasses() {
        File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
        for (Class<?> clazz : ClassUtils.getClasses(jarFile, getClass().getPackage().getName())) {
            if (clazz.getAnnotation(Command.class) != null) commandClasses.add(clazz);
            if (clazz.getAnnotation(EventListener.class) != null) listenerClasses.add(clazz);
            if (clazz.getAnnotation(Inject.class) != null) injectClasses.add(clazz);
            if (clazz.getAnnotation(MainManager.class) != null) mainManagerClass = clazz;
        }
    }

    private void injectMainManager(@NotNull Path path) {
        M manager = registerManager(path);
        if (manager == null) {
            Class<?> clazz = mainManagerClass;
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
        if (manager != null) {
            setManager(manager);
            injectClasses.forEach(this::injectIntoStatic);
        } else Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "CANT register or inject main manager !!!");
    }

    private void injectCommands() {
        for (Class<?> clazz : commandClasses) {
            Command annotation = clazz.getAnnotation(Command.class);
            if (annotation != null) {
                try {
                    Object instance = clazz.getConstructor().newInstance();
                    injectIntoInstance(instance);
                    VCommand command = registerCommand(annotation);
                    if (command != null) {
                        command.extractSub(instance);
                        if (command.getName().equalsIgnoreCase(getId())) {
                            BaseSubCmds baseSubCmds = new BaseSubCmds();
                            injectIntoInstance(baseSubCmds);
                            command.extractSub(baseSubCmds);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void injectListeners() {
        for (Class<?> clazz : listenerClasses) {
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

    private void injectIntoStatic(@NotNull Class<?> clazz) {
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

    private void injectIntoInstance(@NotNull Object instance) {
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

    public String getVersion() {
        return getDescription().getVersion();
    }

    public void onLoad() {
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

    public void onEnable() {
        if (manager != null) {
            manager.beforeLoad();
            manager.load();
            manager.afterLoad();
            injectCommands();
            registerCommands();
            injectListeners();
            registerListeners();
            manager.consoleKey("pluginEnabled", getId() + "-" + getVersion());
            afterEnable();
        } else Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Plugin " + getId() + " enable failed !!!!");
    }

    public void onDisable() {
        if (manager != null) {
            beforeDisable();
            if (manager != null) {
                manager.consoleKey("pluginDisabled", getId() + "-" + getVersion());
                if (manager.canSaveOnDisable()) {
                    manager.consoleKey(manager.save() ? "configSaved" : "configSaveFailed");
                }
            }
        }
    }

    public M getManager() {
        return manager;
    }

    public void setManager(M manager) {
        this.manager = manager;
    }

    public Path getRootPath() {
        return getDataFolder().toPath();
    }

    public void registerListener(@NotNull Object listener) {
        if (listener instanceof Listener) {
            getServer().getPluginManager().registerEvents((Listener) listener, this);
        }
    }

    @Nullable
    public VCommand registerCommand(@NotNull Command annotation) {
        VCommand command = new VCommand(annotation.name(),
                annotation.perm().equalsIgnoreCase("admin") ? manager.defAdminPerm() : annotation.perm(),
                annotation.onlyPlayer(), null, manager);
        return registerCommand(command) ? command : null;
    }

    public boolean registerCommand(@NotNull VCommand command) {
        if (commandMap != null) {
            if (commandMap.register(getId(), command)) {
                return true;
            } else manager.consoleKey("commandRegFailed", command.getName(), getName());
        } else manager.consoleKey("nullCommandMap");
        return false;
    }
}
