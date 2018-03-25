package org.soraworld.violet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.config.IIConfig;
import org.soraworld.violet.util.ListUtil;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class VioletPlugin extends JavaPlugin {

    protected IIConfig config;
    protected IICommand command;

    @Override
    public void onEnable() {
        config = registerConfig(this.getDataFolder());
        config.load();
        config.save();
        List<Listener> listeners = registerEvents(config);
        for (Listener listener : listeners) {
            this.getServer().getPluginManager().registerEvents(listener, this);
        }
        command = registerCommand(config);
    }

    @Override
    public void onDisable() {
        config.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return command != null && command.execute(sender, ListUtil.arrayList(args));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return command == null ? new ArrayList<String>() : command.onTabComplete(sender, cmd, alias, args);
    }

    @Nonnull
    protected abstract IIConfig registerConfig(File path);

    @Nonnull
    protected abstract List<Listener> registerEvents(IIConfig config);

    @Nonnull
    protected abstract IICommand registerCommand(IIConfig config);

}
