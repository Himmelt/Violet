package org.soraworld.violet.api;

import java.util.function.Consumer;

/**
 * The interface Manager.
 *
 * @author Himmelt
 */
public interface IManager {
    boolean load();

    boolean save();

    void asyncSave(Consumer<Boolean> callback);

    void backup();

    void asyncBackup(Consumer<Boolean> callback);

    boolean isDebug();

    void setDebug(boolean debug);
}
