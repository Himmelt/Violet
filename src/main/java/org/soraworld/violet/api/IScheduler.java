package org.soraworld.violet.api;

/**
 * @author Himmelt
 */
public interface IScheduler {
    void runTask(Runnable task);

    void runTaskAsync(Runnable task);

    void runTaskLater(Runnable task, long delay);

    void runTaskAsyncLater(Runnable task, long delay);
}
