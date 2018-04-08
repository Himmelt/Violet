package org.soraworld.violet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.config.IIConfig;
import org.soraworld.violet.util.ListUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
        config.afterLoad();
        for (Listener listener : registerEvents(config)) {
            this.getServer().getPluginManager().registerEvents(listener, this);
        }
        command = registerCommand(config);
        afterEnable();
    }

    @Override
    public void onDisable() {
        beforeDisable();
        config.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return command != null && command.execute(sender, ListUtil.arrayList(args));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return command == null ? new ArrayList<String>() : command.getTabCompletions(ListUtil.arrayList(args));
    }

    @Nonnull
    protected abstract IIConfig registerConfig(File path);

    @Nonnull
    protected abstract List<Listener> registerEvents(IIConfig config);

    @Nullable
    protected abstract IICommand registerCommand(IIConfig config);

    protected abstract void afterEnable();

    protected abstract void beforeDisable();

}
