package org.soraworld.violet.log;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * @author Himmelt
 */
public final class Logger {

    private String lastFile = "";
    private BufferedWriter writer = null;
    private final Object lock = new Object();
    private final ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<>(1000);
    private static final Pattern TRUE_COLOR_PATTERN = Pattern.compile("(?i)\u00A7[0-9a-fk-or]");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("[HH:mm:ss.SSS] ");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @SuppressWarnings("InfiniteLoopStatement")
    public Logger(@NotNull final Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            while (true) {
                if (!queue.isEmpty()) {
                    Message msg = queue.poll();
                    if (msg != null) {
                        try {
                            String logFile = DATE_FORMAT.format(msg.date) + ".log";
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
                                writer.write(TIME_FORMAT.format(msg.date) + TRUE_COLOR_PATTERN.matcher(msg.text).replaceAll(""));
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

    public synchronized void log(@NotNull String message) {
        queue.offer(new Message(message));
        if (queue.size() == 1) {
            synchronized (lock) {
                lock.notify();
            }
        }
    }
}
