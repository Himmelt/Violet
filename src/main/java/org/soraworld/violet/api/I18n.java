package org.soraworld.violet.api;

import org.jetbrains.annotations.NotNull;

/**
 * @author Himmelt
 */
public interface I18n {
    boolean setLang(String lang);

    String getLang();

    String trans(@NotNull String key, Object... args);

    boolean extract();
}
