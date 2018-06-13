package org.soraworld.violet;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.soraworld.violet.command.VioletCommand;
import org.soraworld.violet.config.VioletManager;
import org.soraworld.violet.config.VioletSetting;
import org.soraworld.violet.constant.Violets;
import org.soraworld.violet.listener.EventListener;
import rikka.RikkaAPI;
import rikka.api.command.CommandArgs;
import rikka.api.command.ExecuteResult;

import java.nio.file.Path;

public class VioletBukkit extends JavaPlugin {

    protected VioletManager manager;
    protected VioletCommand command;

    public void onEnable() {
        initPlugin(getDataFolder().toPath());
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.command.execute(RikkaAPI.getCommandSender(sender), new CommandArgs(args)) == ExecuteResult.SUCCESS;
    }


    protected void initPlugin(Path path) {
        manager = new VioletManager(path, new VioletSetting());
        manager.load();
        command = new VioletCommand(Violets.PERM_ADMIN, false, manager, Violets.PLUGIN_ID);
    }

}
