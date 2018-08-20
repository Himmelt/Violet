package org.soraworld.violet.api;

import org.soraworld.violet.util.ChatColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IManager extends ILogger {

    @Nonnull
    String defChatHead();

    @Nullable
    String defAdminPerm();

    @Nonnull
    ChatColor defChatColor();

    boolean load();

    boolean save();

    void afterLoad();

    String getLang();

    void setLang(@Nonnull String lang);

    boolean isDebug();

    void setDebug(boolean debug);

    String trans(@Nonnull String key, Object... args);

    void console(@Nonnull String text);

    void consoleKey(@Nonnull String key, Object... args);

    void broadcast(@Nonnull String text);

    void broadcastKey(@Nonnull String key, Object... args);

    void println(@Nonnull String text);
}
