package org.soraworld.violet.plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.command.BaseSubCmds;
import org.soraworld.violet.command.VCommand;
import org.soraworld.violet.inject.Command;
import org.soraworld.violet.inject.EventListener;
import org.soraworld.violet.inject.MainManager;
import org.soraworld.violet.manager.IManager;
import org.soraworld.violet.manager.VManager;
import org.soraworld.violet.util.ChatColor;
import org.soraworld.violet.util.ClassUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

public class SpongePlugin<M extends VManager> implements IPlugin<M> {

    @Inject
    @ConfigDir(sharedRoot = false)
    protected Path path;
    @Inject
    protected PluginContainer container;

    protected M manager;
    protected Class<?> mainManagerClass;
    protected final HashSet<Class<?>> injectClasses = new HashSet<>();
    protected final HashSet<Class<?>> commandClasses = new HashSet<>();
    protected final HashSet<Class<?>> listenerClasses = new HashSet<>();

    {
        scanJarPackageClasses();
    }

    private void scanJarPackageClasses() {
        File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
        for (Class<?> clazz : ClassUtils.getClasses(jarFile, getClass().getPackage().getName())) {
            if (clazz.getAnnotation(Command.class) != null) commandClasses.add(clazz);
            if (clazz.getAnnotation(EventListener.class) != null) listenerClasses.add(clazz);
            if (clazz.getAnnotation(org.soraworld.violet.inject.Inject.class) != null) injectClasses.add(clazz);
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
        } else Sponge.getServer().getConsole().sendMessage(Text.of(ChatColor.RED + "CANT register or inject main manager !!!"));
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
            org.soraworld.violet.inject.Inject inject = field.getAnnotation(org.soraworld.violet.inject.Inject.class);
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
            org.soraworld.violet.inject.Inject inject = field.getAnnotation(org.soraworld.violet.inject.Inject.class);
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

    @Listener
    public void onLoad(GamePreInitializationEvent event) {
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

    @Listener
    public void onEnable(GameInitializationEvent event) {
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
        } else Sponge.getServer().getConsole().sendMessage(Text.of(ChatColor.RED + "Plugin " + getId() + " enable failed !!!!"));
    }

    @Listener
    public void onDisable(GameStoppingServerEvent event) {
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

    public Path getRootPath() {
        if (path == null) path = new File("config", getId()).toPath();
        return path;
    }

    public String getId() {
        return container.getId();
    }

    public String getName() {
        return container.getName();
    }

    public String getVersion() {
        return container.getVersion().orElse("x.y.z");
    }

    public boolean isEnabled() {
        return Sponge.getPluginManager().isLoaded(container.getId());
    }

    public M getManager() {
        return manager;
    }

    public void setManager(M manager) {
        this.manager = manager;
    }

    public void registerListener(@NotNull Object listener) {
    }

    @Nullable
    public VCommand registerCommand(@NotNull Command annotation) {
        return null;
    }

    public boolean registerCommand(@NotNull VCommand command) {
        Sponge.getCommandManager().register(this, command, command.getAliases());
        return true;
    }
}
