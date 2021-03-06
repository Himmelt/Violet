package org.soraworld.violet.log;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * @author Himmelt
 */
public final class Logger {

    private String lastFile = "";
    private BufferedWriter writer = null;
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final Object lock = new Object();
    private final ExecutorService service;
    private final ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<>(1000);
    private static final Pattern TRUE_COLOR_PATTERN = Pattern.compile("(?i)\u00A7[0-9a-fk-or]");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static int NEXT_LOGGER_ID = 0;

    public Logger(@NotNull final String pluginId, @NotNull final Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        service = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), task -> {
            Thread thread = Executors.defaultThreadFactory().newThread(task);
            thread.setName("Logger-" + pluginId + "#" + NEXT_LOGGER_ID++);
            return thread;
        });
        service.execute(() -> {
            while (!shutdown.get()) {
                if (!queue.isEmpty()) {
                    Message msg = queue.poll();
                    if (msg != null) {
                        try {
                            String logFile = DATE_FORMAT.format(msg.time) + ".log";
                            if (!logFile.equalsIgnoreCase(lastFile)) {
                                if (writer != null) {
                                    writer.flush();
                                    writer.close();
                                    writer = null;
                                }
                                lastFile = logFile;
                            }
                            if (writer == null) {
                                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.resolve(lastFile).toFile(), true), StandardCharsets.UTF_8));
                            }
                            if (msg.text != null) {
                                writer.write("[" + TIME_FORMAT.format(msg.time) + "] " + TRUE_COLOR_PATTERN.matcher(msg.text).replaceAll(""));
                                writer.newLine();
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (writer != null) {
                        try {
                            writer.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (shutdown.get()) {
                        return;
                    }
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void shutdown() {
        shutdown.set(true);
        synchronized (lock) {
            lock.notify();
        }
        service.shutdown();
    }

    public synchronized void log(@NotNull String message) {
        queue.offer(new Message(message));
        if (queue.size() == 1) {
            synchronized (lock) {
                lock.notify();
            }
        }
    }
}
