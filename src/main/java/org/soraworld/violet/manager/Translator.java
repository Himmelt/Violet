package org.soraworld.violet.manager;

import org.jetbrains.annotations.NotNull;

public interface Translator {
    @NotNull
    String trans(@NotNull String lang, @NotNull String key, Object... args);
}
