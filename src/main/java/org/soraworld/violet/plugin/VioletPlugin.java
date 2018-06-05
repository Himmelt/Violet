package org.soraworld.violet.plugin;

import org.soraworld.violet.config.Settings;
import org.soraworld.violet.config.VioletManager;
import rikka.api.command.ICommandSender;
import rikka.api.command.IICommand;

import java.nio.file.Path;
import java.util.ArrayList;

public abstract class VioletPlugin {

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

    public boolean execute(ICommandSender sender, ArrayList<String> args) {
        System.out.println("execute");
        return command != null && command.execute(sender, args);
    }

    protected abstract Settings regSettings();

    protected abstract IICommand regCommand();

    protected abstract void afterEnable();

}
