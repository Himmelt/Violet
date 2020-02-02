package org.soraworld.violet.log;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * @author Himmelt
 */
final class Message {
    final String text;
    final Date date = new Date();

    Message(@NotNull String text) {
        this.text = text;
    }
}
