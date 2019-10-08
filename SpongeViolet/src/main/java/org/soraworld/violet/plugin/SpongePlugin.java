package org.soraworld.violet.plugin;

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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author Himmelt
 */
public class SpongePlugin<M extends VManager> implements IPlugin<M> {

    @javax.inject.Inject
    @ConfigDir(sharedRoot = false)
    protected Path path;
    @javax.inject.Inject
    protected PluginContainer container;

    protected M manager;
    protected Class<?> mainManagerClass;
    protected final HashSet<Class<?>> injectClasses = new HashSet<>();
    protected final HashSet<Class<?>> commandClasses = new HashSet<>();
    protected final HashSet<Class<?>> listenerClasses = new HashSet<>();

    private void scanJarPackageClasses() {
        container.getSource().ifPresent(path -> {
            File jarFile = path.toFile();
            if (jarFile.exists()) {
                for (Class<?> clazz : ClassUtils.getClasses(jarFile, getClass().getPackage().getName())) {
                    if (clazz.getAnnotation(Command.class) != null) {
                        commandClasses.add(clazz);
                    }
                    if (clazz.getAnnotation(EventListener.class) != null) {
                        listenerClasses.add(clazz);
                    }
                    if (clazz.getAnnotation(MainManager.class) != null) {
                        mainManagerClass = clazz;
                    }
                    if (clazz.getAnnotation(Inject.class) != null) {
                        injectClasses.add(clazz);
                    }
                }
            } else {
                Sponge.getServer().getConsole().sendMessage(Text.of(ChatColor.RED + "Plugin Jar File NOT exist !!!"));
            }
        });
    }

    private void injectMainManager(@NotNull Path path) {
        M manager = registerManager(path);
        if (manager == null) {
            Class<?> clazz = mainManagerClass;
            if (clazz != null && IManager.class.isAssignableFrom(clazz)) {
                Sponge.getServer().getConsole().sendMessage(
                        Text.of("[" + getName() + "] Injecting @MainManager class - " + clazz.getName())
                );
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
        } else {
            Sponge.getServer().getConsole().sendMessage(Text.of(ChatColor.RED + getName() + " CANT register or inject main manager !!!"));
        }
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
                        command.extractTab(instance);
                        if (clazz != BaseSubCmds.class && command.getName().equalsIgnoreCase(getId())) {
                            BaseSubCmds baseSubCmds = new BaseSubCmds();
                            injectIntoInstance(baseSubCmds);
                            command.extractSub(baseSubCmds);
                            command.extractTab(baseSubCmds);
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
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }
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
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
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

    @Listener
    public void onLoad(GamePreInitializationEvent event) {
        beforeLoad();
        scanJarPackageClasses();
        registerInjectClasses();
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
            manager.checkUpdate(Sponge.getServer().getConsole());
        } else {
            Sponge.getServer().getConsole().sendMessage(Text.of(ChatColor.RED + "Plugin " + getId() + " enable failed !!!!"));
        }
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

    @Override
    public Path getRootPath() {
        if (path == null) {
            path = new File("config", getId()).toPath();
        }
        return path;
    }

    @Override
    public void beforeLoad() {
    }

    @Override
    public String getId() {
        return container.getId();
    }

    @Override
    public String getName() {
        return container.getName();
    }

    @Override
    public String getVersion() {
        return container.getVersion().orElse("x.y.z");
    }

    @Override
    public boolean isEnabled() {
        return Sponge.getPluginManager().isLoaded(container.getId());
    }

    @Override
    public M getManager() {
        return manager;
    }

    @Override
    public void setManager(M manager) {
        this.manager = manager;
    }

    @Override
    public String updateUrl() {
        String website = container.getUrl().orElse("https://github.com/Himmelt/" + container.getName());
        if (!website.endsWith("/")) {
            website += "/";
        }
        return website + "releases/latest";
    }

    @Override
    public void registerInjectClass(@NotNull Class<?> clazz) {
        if (clazz.getAnnotation(Command.class) != null) {
            commandClasses.add(clazz);
        }
        if (clazz.getAnnotation(EventListener.class) != null) {
            listenerClasses.add(clazz);
        }
        if (clazz.getAnnotation(Inject.class) != null) {
            injectClasses.add(clazz);
        }
    }

    public void registerListener(@NotNull Object listener) {
        Sponge.getEventManager().registerListeners(this, listener);
    }

    @Nullable
    public VCommand registerCommand(@NotNull Command annotation) {
        String perm = annotation.perm();
        perm = manager.mappingPerm(perm);
        VCommand command = new VCommand(annotation.name(), perm, annotation.onlyPlayer(), null, manager);
        command.setAliases(Arrays.asList(annotation.aliases()));
        command.setTabs(Arrays.asList(annotation.tabs()));
        command.setUsage(annotation.usage());
        return registerCommand(command) ? command : null;
    }

    public boolean registerCommand(@NotNull VCommand command) {
        List<String> aliases = new ArrayList<>();
        aliases.add(command.getName());
        aliases.addAll(command.getAliases());
        Sponge.getCommandManager().register(this, command, aliases);
        return true;
    }
}
