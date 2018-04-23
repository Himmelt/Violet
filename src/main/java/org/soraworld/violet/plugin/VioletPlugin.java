package org.soraworld.violet.plugin;

import org.soraworld.rikka.command.CommandSource;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.config.Settings;
import org.soraworld.violet.config.VioletManager;

import java.nio.file.Path;
import java.util.ArrayList;

public abstract class VioletPlugin implements IPlugin {

    protected IICommand command;
    protected VioletManager manager;

    public void loadConfig(Path path) {
        manager = new VioletManager(path, regSettings());
        manager.load();
        command = regCommand();
    }

    public void onDisable() {
        System.out.println("plugin onDisable!!");
    }

    public boolean execute(CommandSource sender, ArrayList<String> args) {
        System.out.println("execute");
        return command != null && command.execute(sender, args);
    }

    protected abstract Settings regSettings();

    protected abstract IICommand regCommand();

    protected abstract void afterEnable();

}
