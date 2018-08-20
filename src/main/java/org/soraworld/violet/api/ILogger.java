package org.soraworld.violet.api;

public interface ILogger {
    void info(String message);

    void debug(String message);

    void warn(String message);

    void error(String message);
}
