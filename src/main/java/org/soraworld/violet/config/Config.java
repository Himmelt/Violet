package org.soraworld.violet.config;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.soraworld.violet.constant.Violets;

import javax.annotation.Nonnull;
import java.io.File;

public class Config extends IIConfig {

    public Config(File path, Plugin plugin) {
        super(path, plugin);
    }

    protected void loadOptions() {
    }

    protected void saveOptions() {
    }

    @Nonnull
    protected ChatColor defaultChatColor() {
        return ChatColor.DARK_PURPLE;
    }

    @Nonnull
    protected String defaultChatHead() {
        return Violets.PLUGIN_NAME;
    }

}
