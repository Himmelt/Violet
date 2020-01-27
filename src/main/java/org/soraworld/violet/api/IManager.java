package org.soraworld.violet.api;

/**
 * The interface Manager.
 *
 * @author Himmelt
 */
public interface IManager {
    void consoleKey(String key, String... args);

    boolean load();

    String getLang();

    boolean setLang(String first);

    void sendKey(ICommandSender sender, String setLang, String lang);

    void asyncSave(Object o);

    boolean isDebug();

    void setDebug(boolean b);

    void sendKey(ICommandSender sender, String s);

    boolean reExtract();

    void asyncBackUp(ICommandSender sender);

    void debug(Throwable e);

    String mappingPerm(String perm);

    String trans(String usage);
}
