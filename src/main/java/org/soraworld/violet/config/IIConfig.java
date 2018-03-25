package org.soraworld.violet.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.io.File;

public abstract class IIConfig {

    private final File file;
    private final YamlConfiguration yaml = new YamlConfiguration();

    public final IILang iiLang;
    public final Plugin plugin;
    public final IIChat iiChat;

    private boolean debug = false;

    public IIConfig(File path, Plugin plugin) {
        this.file = new File(path, "config.yml");
        this.iiLang = new IILang(new File(path, "lang"), this);
        this.plugin = plugin;
        this.iiChat = new IIChat(defaultChatHead(), defaultChatColor());
    }

    public boolean load() {
        if (!file.exists()) {
            setLang(yaml.getString("lang"));
            save();
            return true;
        }
        try {
            yaml.load(file);
            debug = yaml.getBoolean("debug");
            setLang(yaml.getString("lang"));
            loadOptions();
        } catch (Throwable e) {
            if (debug) e.printStackTrace();
            iiChat.console("&cConfig file load exception !!!");
            return false;
        }
        return true;
    }

    public boolean save() {
        try {
            yaml.set("debug", debug);
            yaml.set("lang", iiLang.getLang());
            saveOptions();
            yaml.save(file);
        } catch (Throwable e) {
            if (debug) e.printStackTrace();
            iiChat.console("&cConfig file save exception !!!");
            return false;
        }
        return true;
    }

    public boolean debug() {
        return debug;
    }

    public void debug(boolean debug) {
        this.debug = debug;
    }

    public void setLang(String lang) {
        iiLang.setLang(lang);
        iiChat.setHead(iiLang.format("chatHead"));
    }

    public String getLang() {
        return iiLang.getLang();
    }

    protected abstract void loadOptions();

    protected abstract void saveOptions();

    @Nonnull
    protected abstract ChatColor defaultChatColor();

    @Nonnull
    protected abstract String defaultChatHead();

}
