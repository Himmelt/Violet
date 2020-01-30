package org.soraworld.violet.api;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * The interface Manager.
 *
 * @author Himmelt
 */
public interface IManager {
    boolean load();

    boolean save();

    void asyncSave(@Nullable Consumer<Boolean> callback);

    boolean backup();

    void asyncBackup(@Nullable Consumer<Boolean> callback);

    boolean isDebug();

    void setDebug(boolean debug);
}
