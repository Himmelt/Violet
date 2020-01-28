package org.soraworld.violet.core;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.api.IConfig;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.asm.ClassInfo;
import org.soraworld.violet.asm.PluginScanner;
import org.soraworld.violet.command.CommandCore;
import org.soraworld.violet.inject.Command;
import org.soraworld.violet.inject.Injector;
import org.soraworld.violet.inject.Manager;

import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * @author Himmelt
 */
public final class PluginCore {

    private final IPlugin plugin;
    private final Set<ClassInfo> classes;
    private final Injector injector = new Injector();
    private final TreeMap<Integer, List<Consumer<ClassInfo>>> injectorMap = new TreeMap<>();
    private static final ArrayList<IPlugin> PLUGINS = new ArrayList<>();

    public PluginCore(@NotNull IPlugin plugin) {
        this.plugin = plugin;
        classes = PluginScanner.scan(plugin.getJarFile());
        if (!PLUGINS.contains(plugin)) {
            PLUGINS.add(plugin);
        }
    }

    /**
     * Add injector.
     *
     * @param priority the priority 0 - 10
     * @param injector the injector
     */
    public void addInjector(int priority, @NotNull Consumer<ClassInfo> injector) {
        priority = Math.max(0, Math.min(10, priority));
        injectorMap.computeIfAbsent(priority, i -> new ArrayList<>()).add(injector);
    }

    public void inject() {
        for (int i = 0; i <= 10; i++) {
            List<Consumer<ClassInfo>> injectors = injectorMap.get(i);
            if (injectors != null) {
                classes.forEach(clazz -> injectors.forEach(injector -> injector.accept(clazz)));
            }
        }
    }

    private void injectValues() {
        injector.addValue(plugin);
        injector.addValue(this);
        injector.addValue(plugin.getManager());
        injector.addValue(manager);
    }

    private void addInjectors(ClassLoader loader, Path rootPath) {
        addInjector(0, info -> {
            if (info.hasAnnotation(Manager.class)) {
                manager.console("Try construct @Manager -> " + info.getName());
                try {
                    Class<?> clazz = Class.forName(info.getName(), false, loader);
                    if (clazz != null && IConfig.class.isAssignableFrom(clazz)) {
                        for (Constructor<?> constructor : clazz.getConstructors()) {
                            Class<?>[] params = constructor.getParameterTypes();
                            if (params.length == 2 && IPlugin.class.isAssignableFrom(params[0]) && Path.class.equals(params[1])) {
                                constructor.setAccessible(true);
                                try {
                                    manager.setConfig((IConfig) constructor.newInstance(this, rootPath));
                                    break;
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        addInjector(1, info -> {
            if (info.hasAnnotation(Command.class)) {
                manager.console("Try construct @Command -> " + info.getName());
                try {
                    Class<?> clazz = Class.forName(info.getName(), false, loader);
                    try {
                        Object instance = clazz.getConstructor().newInstance();
                        injector.inject(instance);
                        CommandCore command = registerCommand(annotation);
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
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        addInjector(1, info -> {

        });
    }

    public void onLoad() {
        injectValues();
        Path rootPath = plugin.getRootPath();
        ClassLoader loader = plugin.getClassLoader();
        if (Files.notExists(rootPath)) {
            try {
                Files.createDirectories(rootPath);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        addInjectors(loader, rootPath);
        plugin.onPluginLoad();
    }

    public void onEnable() {
        inject();
        manager.load();
        plugin.onPluginEnable();
        manager.consoleKey("pluginEnabled", plugin.getId() + "-" + plugin.getVersion());
    }

    public void onDisable() {
        plugin.onPluginDisable();
        manager.consoleKey("pluginDisabled", plugin.getId() + "-" + plugin.getVersion());
        if (manager.isSaveOnDisable()) {
            manager.consoleKey(manager.save() ? "configSaved" : "configSaveFailed");
        }
    }
}
