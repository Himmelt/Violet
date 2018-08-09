package org.soraworld.violet;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.command.CommandArgs;
import org.soraworld.violet.command.VioletCommand;
import org.soraworld.violet.config.VioletManager;
import org.soraworld.violet.config.VioletSettings;
import org.soraworld.violet.constant.Violets;
import org.soraworld.violet.listener.EventListener;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class VioletPlugin extends JavaPlugin implements IPlugin {

    protected VioletManager manager;
    protected VioletCommand command;

    public void onEnable() {
        Path path = getDataFolder().toPath();
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (Throwable ignored) {
            }
        }
        initPlugin(path);
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.command.execute(sender, new CommandArgs(args));
        return true;
    }


    protected void initPlugin(Path path) {
        manager = new VioletManager(this, path, new VioletSettings());
        manager.load();
        command = new VioletCommand(Violets.PERM_ADMIN, false, manager, Violets.PLUGIN_ID);
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return this.command.tabCompletions(new CommandArgs(args));
    }

    public String getId() {
        return Violets.PLUGIN_ID;
    }

}
