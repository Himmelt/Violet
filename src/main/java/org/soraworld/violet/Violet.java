package org.soraworld.violet;

import org.bukkit.event.Listener;
import org.soraworld.violet.command.CommandViolet;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.config.Config;
import org.soraworld.violet.config.IIConfig;
import org.soraworld.violet.config.VLang;
import org.soraworld.violet.constant.Violets;
import org.soraworld.violet.listener.EventListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Violet extends VioletPlugin {

    private static VLang vLang;

    @Nonnull
    protected IIConfig registerConfig(File path) {
        Config config = new Config(path, this);
        vLang = config.vLang;
        return config;
    }

    @Nonnull
    protected List<Listener> registerEvents(IIConfig config) {
        ArrayList<Listener> listeners = new ArrayList<>();
        listeners.add(new EventListener(config, this));
        return listeners;
    }

    @Nullable
    protected IICommand registerCommand(IIConfig config) {
        return new CommandViolet(Violets.PLUGIN_ID, null, config, this);
    }

    protected void afterEnable() {

    }

    protected void beforeDisable() {

    }

    public static String translate(String lang, String key, Object... args) {
        if (vLang == null) return key;
        return vLang.format(lang, key, args);
    }

}
