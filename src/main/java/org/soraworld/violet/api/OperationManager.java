package org.soraworld.violet.api;

import rikka.api.command.ICommandSender;

public interface OperationManager {

    void sendMsg(ICommandSender sender, String msg);

    void sendKey(ICommandSender sender, String key, Object... args);

    void console(String msg);

    void consoleKey(String key, Object... args);

    void broadcast(String msg);

    void broadcastKey(String key, Object... args);

    void println(String msg);

}
