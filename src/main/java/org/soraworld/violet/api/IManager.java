package org.soraworld.violet.api;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IManager {

    @Nonnull
    String defChatHead();

    @Nullable
    String defAdminPerm();

    @Nonnull
    ChatColor defChatColor();

    boolean load();

    boolean save();

    void afterLoad();

    String getLang();

    void setLang(@Nonnull String lang);

    boolean isDebug();

    void setDebug(boolean debug);

    /* No Color */
    String trans(@Nonnull String key, Object... args);

    /* With Color */
    void send(@Nonnull CommandSender sender, @Nonnull String msg);

    /* With Color */
    void sendKey(@Nonnull CommandSender sender, @Nonnull String key, Object... args);

    /* With Color */
    void console(@Nonnull String format);

    /* With Color */
    void consoleKey(@Nonnull String key, Object... args);

    /* With Color */
    void broadcast(@Nonnull String format);

    /* With Color */
    void broadcastKey(@Nonnull String key, Object... args);

    /* No Color */
    void println(@Nonnull String plain);
}
