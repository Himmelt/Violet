package org.soraworld.violet.log;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author Himmelt
 */
class Message {
    private final String text;
    private final long time = System.currentTimeMillis();

    private static final Pattern TRUE_COLOR_PATTERN = Pattern.compile("(?i)\u00A7[0-9a-fk-or]");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("[HH:mm:ss.SSS] ");

    Message(@NotNull String text) {
        this.text = text;
    }

    String getText() {
        return TIME_FORMAT.format(new Date(time)) + TRUE_COLOR_PATTERN.matcher(text).replaceAll("");
    }
}
