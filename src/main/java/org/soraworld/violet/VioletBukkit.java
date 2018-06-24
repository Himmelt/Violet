package org.soraworld.violet;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.soraworld.violet.command.VioletCommand;
import org.soraworld.violet.config.VioletManager;
import org.soraworld.violet.constant.Violets;
import org.soraworld.violet.listener.EventListener;
import rikka.RikkaAPI;
import rikka.api.IPlugin;
import rikka.api.command.CommandArgs;
import rikka.api.command.ExecuteResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class VioletBukkit extends JavaPlugin implements IPlugin {

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
        return this.command.execute(RikkaAPI.getCommandSender(sender), new CommandArgs(args)) == ExecuteResult.SUCCESS;
    }


    protected void initPlugin(Path path) {
        manager = new VioletManager(this, path);
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
