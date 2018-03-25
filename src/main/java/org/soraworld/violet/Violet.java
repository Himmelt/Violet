package org.soraworld.violet;

import org.bukkit.event.Listener;
import org.soraworld.violet.command.CommandViolet;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.config.Config;
import org.soraworld.violet.config.IIConfig;
import org.soraworld.violet.config.IILang;
import org.soraworld.violet.constant.Constant;
import org.soraworld.violet.listener.EventListener;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Violet extends VioletPlugin {

    private static IILang iiLang;

    @Nonnull
    protected IIConfig registerConfig(File path) {
        IIConfig config = new Config(path, this);
        iiLang = config.iiLang;
        return config;
    }

    @Nonnull
    protected List<Listener> registerEvents(IIConfig config) {
        ArrayList<Listener> listeners = new ArrayList<>();
        listeners.add(new EventListener(config, this));
        return listeners;
    }

    @Nonnull
    protected IICommand registerCommand(IIConfig config) {
        return new CommandViolet(Constant.PLUGIN_ID, config, this);
    }

    public static String translate(String key, Object... args) {
        if (iiLang == null) return String.format(key, args);
        return iiLang.format(key, args);
    }

}
