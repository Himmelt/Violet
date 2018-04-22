package org.soraworld.violet.plugin;

import org.soraworld.violet.api.VioletSender;
import org.soraworld.violet.config.VioletManager;

import java.nio.file.Path;
import java.util.ArrayList;

public abstract class VioletPlugin {

    protected VioletManager manager;

    public void onLoad() {

    }

    public void onEnable(Path path) {
        System.out.println("plugin onEnable!!");
    }

    public void onDisable() {
        System.out.println("plugin onDisable!!");
    }

    public boolean execute(VioletSender sender, ArrayList<String> args) {
        System.out.println("execute");
        return true;
    }

}
