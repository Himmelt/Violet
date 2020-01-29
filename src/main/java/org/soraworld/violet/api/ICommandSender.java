package org.soraworld.violet.api;

import org.soraworld.violet.text.ChatType;

/**
 * @author Himmelt
 */
public interface ICommandSender {
    boolean hasPermission(String permission);

    void sendMessage(String message);

    void sendMessageKey(String key, Object... args);

    void sendMessage(ChatType type, String message);

    void sendMessageKey(ChatType type, String key, Object... args);
}
