package org.soraworld.violet.api;

import org.bukkit.command.CommandSender;

public interface IManager {

    void sendMsg(CommandSender sender, String msg);

    void sendKey(CommandSender sender, String key, Object... args);

    void console(String msg);

    void consoleKey(String key, Object... args);

    void broadcast(String msg);

    void broadcastKey(String key, Object... args);

    void println(String msg);

    String lang();

    boolean debug();

    void debug(boolean debug);

}
