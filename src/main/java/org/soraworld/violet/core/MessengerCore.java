package org.soraworld.violet.core;

import org.jetbrains.annotations.NotNull;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.manager.Translator;
import org.soraworld.violet.text.JsonText;
import org.soraworld.violet.util.ChatColor;

/**
 * @author Himmelt
 */
public final class MessengerCore {
    @Setting
    private boolean debug = false;

    private String colorHead;
    private String plainHead;
    private JsonText jsonHead;

    private final IPlugin plugin;

    public MessengerCore(IPlugin plugin){
        this.plugin = plugin;

    }

    /**
     * Def chat color chat color.
     *
     * @return the chat color
     */
    private ChatColor defChatColor();

    /**
     * Console.
     *
     * @param text the text
     */
    public void console(String text);

    /**
     * Broadcast.
     *
     * @param message the message
     */
    public void broadcast(String message);
    /**
     * Debug key.
     *
     * @param key  the key
     * @param args the args
     */
    public void debugKey(String key, Object... args) {
        if (isDebug()) {
            consoleKey(key, args);
        }
    }

    /**
     * Debug.
     *
     * @param text the text
     */
    public void debug(String text) {
        if (isDebug()) {
            console(text);
        }
    }

    /**
     * Debug.
     *
     * @param e the e
     */
    public void debug(Throwable e) {
        if (isDebug()) {
            e.printStackTrace();
        }
    }

    /**
     * Broadcast key.
     *
     * @param key  the key
     * @param args the args
     */
    public void broadcastKey(String key, Object... args) {
        broadcast(trans(key, args));
    }

    /**
     * Console key.
     *
     * @param key  the key
     * @param args the args
     */
    public void consoleKey(String key, Object... args) {
        console(trans(key, args));
    }

    public void log(@NotNull String text) {

    }

    public void logKey(@NotNull String key, Object... args) {
        log(trans(key, args));
    }

    public void consoleLog(@NotNull String text) {
        console(text);
        log(text);
    }

    public void consoleLogKey(@NotNull String key, Object... args) {
        String text = trans(key, args);
        console(text);
        log(text);
    }

    /**
     * Println.
     *
     * @param text the text
     */
    public void println(@NotNull String text) {
        System.out.println(plainHead + text);
    }

    /**
     * 设置聊天前缀.
     *
     * @param head 前缀
     */
    public void setHead(String head) {
        this.colorHead = defChatColor() + ChatColor.colorize(head) + ChatColor.RESET;
        this.plainHead = ChatColor.stripColor(colorHead);
        this.jsonHead = new JsonText(colorHead);
    }

    /**
     * The Translator.
     */
    static Translator translator = null;
}
