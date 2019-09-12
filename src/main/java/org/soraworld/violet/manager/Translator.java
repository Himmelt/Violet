package org.soraworld.violet.manager;

import org.jetbrains.annotations.NotNull;

/**
 * The interface Translator.
 *
 * @author Himmelt
 */
public interface Translator {
    /**
     * Trans string.
     *
     * @param lang the lang
     * @param key  the key
     * @param args the args
     * @return the string
     */
    @NotNull
    String trans(@NotNull String lang, @NotNull String key, Object... args);
}
