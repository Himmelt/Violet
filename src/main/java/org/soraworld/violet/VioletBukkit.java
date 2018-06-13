package org.soraworld.violet;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.soraworld.violet.command.VioletCommand;
import org.soraworld.violet.config.Settings;
import org.soraworld.violet.config.VioletManager;
import org.soraworld.violet.config.VioletSettings;
import org.soraworld.violet.constant.Violets;
import org.soraworld.violet.listener.EventListener;
import rikka.RikkaAPI;
import rikka.api.command.CommandArgs;
import rikka.api.command.CommandResult;
import rikka.api.command.IICommand;

import java.nio.file.Path;

public class VioletBukkit extends JavaPlugin {

    protected VioletManager manager;
    protected VioletCommand command;

    public void onEnable() {
        loadConfig(getDataFolder().toPath());
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
    }

    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.command.execute(RikkaAPI.getCommandSender(sender), new CommandArgs(args)) == CommandResult.SUCCESS;
    }


    public void loadConfig(Path path) {
        manager = new VioletManager(path, regSettings());
        manager.load();
    }

    protected Settings regSettings() {
        return new VioletSettings();
    }

    protected void afterEnable() {
    }

    protected IICommand regCommand() {
        return new VioletCommand(Violets.PERM_ADMIN, false, manager, Violets.PLUGIN_ID);
    }

}
