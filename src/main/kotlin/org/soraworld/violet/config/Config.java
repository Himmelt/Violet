package org.soraworld.violet.config;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.soraworld.violet.chat.VLang;
import org.soraworld.violet.constant.Violets;

import javax.annotation.Nonnull;
import java.io.File;

public class Config extends IIConfig {

    public final VLang vLang;

    public Config(File path, Plugin plugin) {
        super(path, plugin);
        this.vLang = new VLang(new File(path, "lang"), this);
    }

    protected void loadOptions() {
    }

    protected void saveOptions() {
    }

    public void afterLoad() {

    }

    @Nonnull
    protected ChatColor defaultChatColor() {
        return ChatColor.DARK_PURPLE;
    }

    @Nonnull
    protected String defaultChatHead() {
        return "[" + Violets.PLUGIN_NAME + "] ";
    }

    @Override
    public String defaultAdminPerm() {
        return Violets.PERM_ADMIN;
    }

}