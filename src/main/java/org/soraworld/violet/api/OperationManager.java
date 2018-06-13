package org.soraworld.violet.api;

import rikka.api.command.ICommandSender;

public interface OperationManager {

    void sendMsg(ICommandSender sender, String msg);

    void sendKey(ICommandSender sender, String key, Object... args);

    void sendVKey(ICommandSender sender, String key, Object... args);

}
