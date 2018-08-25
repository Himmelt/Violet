package org.soraworld.violet.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.command.CommandArgs;
import org.soraworld.violet.command.SpigotCommand;
import org.soraworld.violet.manager.SpigotManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Spigot 插件.
 */
public abstract class SpigotPlugin extends JavaPlugin implements IPlugin {

    /**
     * 管理器.
     */
    protected SpigotManager manager;
    /**
     * 主命令.
     */
    protected SpigotCommand command;

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
        command = registerCommand();
        manager.beforeLoad();
        manager.load();
        manager.afterLoad();
        List<Listener> listeners = registerListeners();
        if (listeners != null && !listeners.isEmpty()) {
            for (Listener listener : listeners) {
                this.getServer().getPluginManager().registerEvents(listener, this);
            }
        }
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

    public void afterEnable() {
    }

    public void beforeDisable() {
    }

    public void onDisable() {
        beforeDisable();
        super.onDisable();
        if (manager != null) manager.consoleKey(Violet.KEY_PLUGIN_DISABLED, getId());
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
     * 注册 Spigot 命令.
     *
     * @return 命令
     */
    @Nonnull
    protected abstract SpigotCommand registerCommand();

    /**
     * 注册监听器.
     *
     * @return 监听器列表
     */
    @Nullable
    protected abstract List<Listener> registerListeners();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) command.execute(((Player) sender), new CommandArgs(args));
        else if (command.nop()) command.execute(sender, new CommandArgs(args));
        else manager.sendKey(sender, Violet.KEY_ONLY_PLAYER);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return command.tabCompletions(new CommandArgs(args));
    }
}
