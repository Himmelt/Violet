package org.soraworld.violet.manager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.NodeOptions;
import org.soraworld.violet.Violets;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.api.IPlugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;

import static java.util.Locale.CHINA;

public abstract class VioletManager implements IManager {

    protected final Path path;
    protected final Path confile;
    protected final IPlugin plugin;
    protected final VioletSettings settings;
    protected final NodeOptions options = NodeOptions.newOptions();
    protected final HashMap<String, String> langMap = new HashMap<>();

    public VioletManager(IPlugin plugin, Path path, VioletSettings settings) {
        this.path = path;
        this.plugin = plugin;
        this.confile = path.resolve(plugin.getId().replace(' ', '_') + ".conf");
        this.settings = settings != null ? settings : new VioletSettings();
    }

    public boolean load() {
        if (Files.notExists(confile)) {
            setLang(CHINA.equals(Locale.getDefault()) ? "zh_cn" : "en_us");
            save();
            return true;
        }
        try {
            FileNode rootNode = new FileNode(confile.toFile(), options);
            rootNode.load();
            rootNode.modify(settings);
            setLang(settings.lang);
            return true;
        } catch (Throwable e) {
            console("&cConfig file load exception !!!");
            if (settings.debug) e.printStackTrace();
            return false;
        }
    }

    public boolean save() {
        try {
            FileNode rootNode = new FileNode(confile.toFile(), options);
            rootNode.extract(settings);
            rootNode.save();
            return true;
        } catch (Throwable e) {
            console("&cConfig file save exception !!!");
            if (settings.debug) e.printStackTrace();
            return false;
        }
    }

    public void setLang(String lang) {
        settings.lang = lang;
        Path langFile = path.resolve("lang").resolve(lang + ".lang");
        boolean extract = false;
        try {
            if (Files.notExists(langFile)) {
                Files.createDirectories(langFile.getParent());
                Files.copy(plugin.getAsset("lang/" + lang + ".lang"), langFile);
            }
            extract = true;
            FileNode langNode = new FileNode(langFile.toFile(), options);
            langNode.load();
            langMap.clear();
            langMap.putAll(langNode.asStringMap());
        } catch (Throwable e) {
            if (extract) console("&cLang file " + lang + " load exception !!!");
            else console("&cLang file " + lang + " extract exception !!!");
            if (settings.debug) e.printStackTrace();
        }
    }

    public String trans(String key) {
        // TODO xx.b.c ?
        return langMap.get(key);
    }

    public String trans(String key, Object... args) {
        String text = langMap.get(key);
        return text == null || text.isEmpty() ? key : String.format(text, args);
    }

    public static String colorize(String text) {
        return text == null ? null : text.replace('&', Violets.COLOR_CHAR);
    }

    public void sendMsg(CommandSender sender, String msg) {
        // TODO
        sender.sendMessage(msg);
    }

    public void sendKey(CommandSender sender, String key, Object... args) {
        sender.sendMessage(colorize(trans(key, args)));
    }

    public void broadcast(String msg) {
        Bukkit.broadcastMessage(msg);
    }

    public void broadcastKey(String key, Object... args) {
        broadcast(trans(key, args));
    }

    public void console(String msg) {
        Bukkit.getConsoleSender().sendMessage(colorize(msg));
    }

    public void consoleKey(String key, Object... args) {
        console(trans(key, args));
    }

    public void println(String msg) {
        // TODO plainHead
        System.out.println("plainHead " + msg);
    }

    public String getLang() {
        return settings.lang;
    }

    public boolean isDebug() {
        return settings.debug;
    }

    public void setDebug(boolean debug) {
        settings.debug = debug;
    }
}
