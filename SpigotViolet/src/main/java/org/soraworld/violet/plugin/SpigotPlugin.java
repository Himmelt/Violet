package org.soraworld.violet.plugin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.command.SpigotBaseSubs;
import org.soraworld.violet.command.SpigotCommand;
import org.soraworld.violet.manager.SpigotManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Spigot 插件.
 */
public abstract class SpigotPlugin extends JavaPlugin implements IPlugin {

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


    /**
     * 主管理器.
     */
    protected SpigotManager manager;

    public void onEnable() {
        Path path = getDataFolder().toPath();
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        manager = registerManager(path);
        manager.beforeLoad();
        manager.load();
        manager.afterLoad();
        List<Listener> listeners = registerListeners();
        if (listeners != null && !listeners.isEmpty()) {
            for (Listener listener : listeners) {
                this.getServer().getPluginManager().registerEvents(listener, this);
            }
        }
        registerCommands();
        manager.consoleKey(Violet.KEY_PLUGIN_ENABLED, getId());
        afterEnable();
    }

    @Nonnull
    public String getId() {
        return getName().toLowerCase().replace(' ', '_');
    }

    @Nonnull
    public String getVersion() {
        return getDescription().getVersion();
    }

    public void onDisable() {
        beforeDisable();
        if (manager != null) {
            manager.consoleKey(Violet.KEY_PLUGIN_DISABLED, getId());
            manager.save();
        }
        super.onDisable();
    }

    /**
     * 注册 Spigot 管理器.
     *
     * @param path 配置文件路径
     * @return 管理器
     */
    @Nonnull
    protected abstract SpigotManager registerManager(Path path);

    /**
     * 注册监听器.
     *
     * @return 监听器列表
     */
    @Nullable
    protected abstract List<Listener> registerListeners();

    /**
     * 注册 Spigot 命令.
     * 使用 {@link SpigotPlugin#register} 注册
     * 默认注册了4个子命令，可以通过 override 此方法修改注册的内容。
     * 建议保留这4个基础子命令 !!
     */
    protected void registerCommands() {
        SpigotCommand command = new SpigotCommand(getId(), manager.defAdminPerm(), false, manager);
        command.extractSub(SpigotBaseSubs.class, "lang");
        command.extractSub(SpigotBaseSubs.class, "debug");
        command.extractSub(SpigotBaseSubs.class, "save");
        command.extractSub(SpigotBaseSubs.class, "reload");
        register(this, command);
    }

    public static void register(SpigotPlugin plugin, SpigotCommand command) {
        if (commandMap != null) {
            if (!commandMap.register(plugin.getId(), command)) {
                System.out.println("Command " + command.getName() + " in plugin " + plugin.getName() + " register failed !!!");
            }
        } else System.out.println("Null commandMap !!!!!!!!!!!!!!!!!!!!");
    }
}
