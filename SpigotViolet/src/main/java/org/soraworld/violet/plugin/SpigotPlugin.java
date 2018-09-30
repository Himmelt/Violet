package org.soraworld.violet.plugin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.soraworld.hocon.node.Paths;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.command.SpigotBaseSubs;
import org.soraworld.violet.command.SpigotCommand;
import org.soraworld.violet.manager.SpigotManager;

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
        manager.consoleKey("pluginEnabled", getId());
        afterEnable();
    }

    public String getId() {
        return getName().toLowerCase().replace(' ', '_');
    }

    public String getVersion() {
        return getDescription().getVersion();
    }

    public void onDisable() {
        beforeDisable();
        if (manager != null) {
            manager.consoleKey("pluginDisabled", getId());
            if (manager.canSaveOnDisable()) manager.save();
        }
    }

    /**
     * 注册 Spigot 管理器.
     *
     * @param path 配置文件路径
     * @return 管理器
     */
    protected abstract SpigotManager registerManager(Path path);

    /**
     * 注册监听器.
     *
     * @return 监听器列表
     */
    protected abstract List<Listener> registerListeners();

    /**
     * 注册 Spigot 命令.<br>
     * 使用 {@link SpigotPlugin#register} 注册<br>
     * 默认注册了6个子命令 save|reload|lang|debug|help|rextract<br>
     * 可以通过 override 此方法修改注册的内容.<br>
     * 建议保留这6个基础子命令 !!<br>
     * 特别建议:<br>
     * 重写此方法时要参考此模板的写法, 由于综合限制, 暂时没想到自动化解决方案.
     */
    protected void registerCommands() {
        SpigotCommand command = new SpigotCommand(getId(), manager.defAdminPerm(), false, manager);
        command.extractSub(SpigotBaseSubs.class, "lang");
        command.extractSub(SpigotBaseSubs.class, "debug");
        command.extractSub(SpigotBaseSubs.class, "save");
        command.extractSub(SpigotBaseSubs.class, "reload");
        command.extractSub(SpigotBaseSubs.class, "help");
        command.extractSub(SpigotBaseSubs.class, "rextract");
        manager.getDisableCmds().forEach(s -> command.removeSub(new Paths(s)));
        register(this, command);
    }

    /**
     * 向服务器注册命令.
     *
     * @param plugin  命令注册到的插件
     * @param command 命令
     */
    public static void register(SpigotPlugin plugin, SpigotCommand command) {
        if (commandMap != null) {
            if (!commandMap.register(plugin.getId(), command)) {
                System.out.println("Command " + command.getName() + " in plugin " + plugin.getName() + " register failed !!!");
            }
        } else System.out.println("Null commandMap !!!!!!!!!!!!!!!!!!!!");
    }
}
