package org.soraworld.violet.core;

import org.jetbrains.annotations.NotNull;

/**
 * @author Himmelt
 */
public interface Translator {
    String trans(@NotNull String lang, @NotNull String key);
}