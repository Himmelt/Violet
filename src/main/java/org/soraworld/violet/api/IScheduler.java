package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;

/**
 * @author Himmelt
 */
public interface IScheduler {
    void runTask(@NotNull Runnable task);

    void runTaskAsync(@NotNull Runnable task);

    void runTaskLater(@NotNull Runnable task, long delay);

    void runTaskLaterAsync(@NotNull Runnable task, long delay);
}
