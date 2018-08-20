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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;

public abstract class SpigotPlugin extends JavaPlugin implements IPlugin {

    protected SpigotManager manager;
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
        manager.load();
        manager.afterLoad();
        command = registerCommand();
        List<Object> listeners = registerListeners();
        if (listeners != null && !listeners.isEmpty()) {
            for (Object listener : listeners) {
                if (listener instanceof Listener) {
                    this.getServer().getPluginManager().registerEvents((Listener) listener, this);
                }
            }
        }
        afterEnable();
    }

    public void onDisable() {
        beforeDisable();
        super.onDisable();
    }

    @Nonnull
    protected abstract SpigotManager registerManager(Path path);

    @Nonnull
    protected abstract SpigotCommand registerCommand();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) command.execute(((Player) sender), new CommandArgs(args));
        else if (command.notOnlyPlayer()) command.execute(sender, new CommandArgs(args));
        else manager.sendKey(sender, Violet.KEY_ONLY_PLAYER);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return command.tabCompletions(new CommandArgs(args));
    }

    public void afterEnable() {
        if (manager != null) manager.consoleKey(Violet.KEY_PLUGIN_ENABLED, getId());
    }

    public void beforeDisable() {
        if (manager != null) manager.consoleKey(Violet.KEY_PLUGIN_DISABLED, getId());
    }

    public void info(String message) {
        getLogger().info(message);
    }

    public void debug(String message) {
        getLogger().info(message);
    }

    public void warn(String message) {
        getLogger().warning(message);
    }

    public void error(String message) {
        getLogger().log(Level.ALL, message);
    }
}
