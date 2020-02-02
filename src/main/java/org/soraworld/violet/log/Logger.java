package org.soraworld.violet.log;

import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.api.IPlugin;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Himmelt
 */
public final class Logger {
    private final Object lock = new Object();
    private final ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<>(1000);
    private ExecutorService service = Executors.newSingleThreadExecutor();

    public Logger(@NotNull IPlugin plugin) {
        service.execute(() -> {
            while (true) {
                if (!queue.isEmpty()) {
                    Message msg = queue.poll();
                    if (msg != null) {
                        println(msg.getText());
                    }
                } else {
                    synchronized (lock) {
                        try {
                            System.out.println("我挂起了！");
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("我被唤醒了！");
                }
            }
        });
    }

    public synchronized void log(@NotNull String message) {
        queue.offer(new Message(message));
        if (queue.size() == 1) {
            synchronized (lock) {
                lock.notify();
            }
        }
    }

    private void println(String message) {
        // TODO
        System.out.println(message);
    }
}
