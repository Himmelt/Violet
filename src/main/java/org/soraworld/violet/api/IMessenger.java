package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;

/**
 * @author Himmelt
 */
public interface IMessenger {

    void console(@NotNull String message);

    void consoleKey(String key, Object... args);

    void log(@NotNull String text);

    void logKey(@NotNull String key, Object... args);

    void consoleLog(@NotNull String text);

    void consoleLogKey(@NotNull String key, Object... args);

    void broadcast(@NotNull String message);

    void broadcastKey(@NotNull String key,Object... args);

    void debug(@NotNull String message);

    void debug(@NotNull Throwable e);

    void debugKey(@NotNull String key, Object... args);

    void notifyOps(@NotNull String message);

    void notifyOpsKey(@NotNull String key, Object... args);
}
