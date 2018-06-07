package org.soraworld.violet.plugin;

import org.soraworld.violet.config.Settings;
import org.soraworld.violet.config.VioletManager;
import rikka.api.command.ICommand;

import java.nio.file.Path;

public abstract class VioletPlugin implements ICommand {

    protected VioletManager manager;

    public void loadConfig(Path path) {
        manager = new VioletManager(path, regSettings());
        manager.load();
    }

    public void onDisable() {
        System.out.println("plugin onDisable!!");
    }

    protected abstract Settings regSettings();

    protected abstract void afterEnable();

}
