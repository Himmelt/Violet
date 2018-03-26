package org.soraworld.violet.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.soraworld.violet.constant.Violets;

import javax.annotation.Nonnull;
import java.io.File;

public abstract class IIConfig {

    public final IILang iiLang;
    public final Plugin plugin;
    public final IIChat iiChat;

    protected boolean debug = false;
    protected final File config_file;
    protected final YamlConfiguration config_yaml = new YamlConfiguration();

    public IIConfig(File path, Plugin plugin) {
        this.config_file = new File(path, "config.yml");
        this.iiLang = new IILang(new File(path, "lang"), this);
        this.plugin = plugin;
        this.iiChat = new IIChat(defaultChatHead(), defaultChatColor());
    }

    public boolean load() {
        if (!config_file.exists()) {
            setLang(config_yaml.getString("lang"));
            save();
            return true;
        }
        try {
            config_yaml.load(config_file);
            debug = config_yaml.getBoolean("debug");
            setLang(config_yaml.getString("lang"));
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
            config_yaml.set("debug", debug);
            config_yaml.set("lang", iiLang.getLang());
            saveOptions();
            config_yaml.save(config_file);
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
        if (iiLang.hasKey(Violets.KEY_CHAT_HEAD)) {
            iiChat.setHead(iiLang.format(Violets.KEY_CHAT_HEAD));
        } else {
            iiChat.setHead(defaultChatHead());
        }
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
