package org.soraworld.violet.manager;

import org.bukkit.ChatColor;
import org.soraworld.violet.Violets;
import org.soraworld.violet.api.IPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;

public class Manager extends VioletManager {

    public Manager(IPlugin plugin, Path path, VioletSettings settings) {
        super(plugin, path, settings);
    }

    @Nonnull
    public String defChatHead() {
        return "[" + Violets.PLUGIN_NAME + "] ";
    }

    @Nonnull
    public ChatColor defChatColor() {
        return ChatColor.DARK_PURPLE;
    }

    @Nullable
    public String defAdminPerm() {
        return Violets.PERM_ADMIN;
    }

    public void afterLoad() {
    }
}
