package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;

/**
 * @author Himmelt
 */
public interface IMessenger {
    void console(String message);

    void consoleKey(String key, Object... args);

    void log(@NotNull String text);

    void logKey(@NotNull String key, Object... args);

    void consoleLog(@NotNull String text);

    void consoleLogKey(@NotNull String key, Object... args);

    void broadcast(String message);

    void broadcastKey(String message);

    void debug(String message);

    void debug(Throwable e);

    void debugKey(String key, Object... args);
}
