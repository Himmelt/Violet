package org.soraworld.violettest;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.plugin.SpigotPlugin;
import org.soraworld.violet.text.ChatColor;

/**
 * @author Himmelt
 */
public final class VioletTest extends SpigotPlugin {
    @Override
    public @NotNull ChatColor chatColor() {
        return ChatColor.AQUA;
    }

    @Override
    public String violetVersion() {
        return "[2.5.0,9.9.9]";
    }
}
