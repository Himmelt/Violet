package org.soraworld.violet.log;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * @author Himmelt
 */
final class Message {
    final String text;
    final LocalDateTime time = LocalDateTime.now();

    Message(@NotNull String text) {
        this.text = text;
    }
}
