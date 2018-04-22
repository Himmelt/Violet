package org.soraworld.violet.plugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.VioletAPI;
import org.soraworld.violet.listener.EventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class VioletBukkit extends JavaPlugin {

    protected VioletPlugin plugin = new Violet();

    public void onEnable() {
        plugin.loadConfig(getDataFolder().toPath());
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
    }

    public void onDisable() {
        plugin.onDisable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return plugin.execute(VioletAPI.getSender(sender), new ArrayList<>(Arrays.asList(args)));
    }

}
