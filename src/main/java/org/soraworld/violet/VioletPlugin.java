package org.soraworld.violet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.command.CommandArgs;
import org.soraworld.violet.command.ICommand;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class VioletPlugin extends JavaPlugin implements IPlugin {

    protected IManager manager;
    protected ICommand command;

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
        for (Listener listener : registerEvents()) {
            this.getServer().getPluginManager().registerEvents(listener, this);
        }
        command = registerCommand();
        afterEnable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) this.command.execute((Player) sender, new CommandArgs(args));
        else if (!this.command.isOnlyPlayer()) this.command.execute(sender, new CommandArgs(args));
        else manager.sendKey(sender, Violets.KEY_ONLY_PLAYER);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return this.command.tabCompletions(new CommandArgs(args));
    }

    public void onDisable() {
        beforeDisable();
        super.onDisable();
    }

    public void afterEnable() {
        manager.consoleKey(Violets.KEY_PLUGIN_ENABLED, getId());
    }

    public void beforeDisable() {
        manager.consoleKey(Violets.KEY_PLUGIN_DISABLED, getId());
    }
}
