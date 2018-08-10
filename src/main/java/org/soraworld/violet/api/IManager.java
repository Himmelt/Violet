package org.soraworld.violet.api;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IManager {

    @Nonnull
    String defChatHead();

    @Nonnull
    ChatColor defChatColor();

    @Nullable
    String defAdminPerm();

    void afterLoad();

    boolean load();

    boolean save();

    String getLang();

    void setLang(String lang);

    void sendMsg(CommandSender sender, String msg);

    void sendKey(CommandSender sender, String key, Object... args);

    void console(String msg);

    void consoleKey(String key, Object... args);

    void broadcast(String msg);

    void broadcastKey(String key, Object... args);

    void println(String msg);

    boolean isDebug();

    void setDebug(boolean debug);
}
