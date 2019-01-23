package org.soraworld.violet.plugin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.api.ICommand;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.command.SpigotCommand;
import org.soraworld.violet.inject.Command;
import org.soraworld.violet.inject.PluginData;
import org.soraworld.violet.manager.SpigotManager;

import java.lang.reflect.Field;
import java.nio.file.Path;

/**
 * Spigot 插件.
 */
public class SpigotPlugin<M extends SpigotManager> extends JavaPlugin implements IPlugin<M> {

    /**
     * 主管理器.
     */
    protected M manager;

    protected final PluginData pluginData = new PluginData();
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

    public SpigotPlugin() {
        scanJarPackageClasses();
    }

    public String getVersion() {
        return getDescription().getVersion();
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
    public ICommand registerCommand(@NotNull Command cmd) {
        SpigotCommand command = new SpigotCommand(cmd.name(),
                cmd.perm().equalsIgnoreCase("admin") ? manager.defAdminPerm() : cmd.perm(),
                cmd.onlyPlayer(), manager);
        return registerCommand(command) ? command : null;
    }

    /**
     * 向服务器注册命令.
     *
     * @param command 命令
     */
    public boolean registerCommand(@NotNull ICommand command) {
        if (commandMap != null) {
            if (command instanceof SpigotCommand && commandMap.register(getId(), (SpigotCommand) command)) {
                pluginData.commands.add(command);
                return true;
            } else manager.consoleKey("commandRegFailed", command.getName(), getName());
        } else manager.consoleKey("nullCommandMap");
        return false;
    }

    public PluginData getPluginData() {
        return pluginData;
    }
}
