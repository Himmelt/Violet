package org.soraworld.violet.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.NodeOptions;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.api.IPlugin;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;

import static org.soraworld.violet.Violets.*;

public abstract class VioletManager implements IManager {

    protected final Path path;
    protected final Path confile;
    protected final IPlugin plugin;
    protected String plainHead;
    protected String colorHead;
    protected final VioletSettings settings;
    protected final NodeOptions options = NodeOptions.newOptions();
    protected HashMap<String, String> langMap = new HashMap<>();

    public VioletManager(IPlugin plugin, Path path, VioletSettings settings) {
        this.path = path;
        this.plugin = plugin;
        this.options.setTranslator(this::trans);
        this.confile = path.resolve(plugin.getId().replace(' ', '_') + ".conf");
        this.settings = settings != null ? settings : new VioletSettings();
        setHead(defChatHead());
    }

    public boolean load() {
        if (Files.notExists(confile)) {
            setLang(Locale.CHINA.equals(Locale.getDefault()) ? "zh_cn" : "en_us");
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

    public String getLang() {
        return settings.lang;
    }

    public void setLang(@Nonnull String lang) {
        settings.lang = lang;
        HashMap<String, String> temp = loadLangMap(lang);
        if (!temp.isEmpty()) langMap = temp;
        else consoleKey("emptyLangMap");
        String head = langMap.get(KEY_CHAT_HEAD);
        if (head != null && !head.isEmpty()) setHead(head);
    }

    private void setHead(@Nonnull String head) {
        this.colorHead = defChatColor() + head.replace('&', COLOR_CHAR) + ChatColor.RESET;
        this.plainHead = COLOR_PATTERN.matcher(colorHead).replaceAll("");
    }

    public boolean isDebug() {
        return settings.debug;
    }

    public void setDebug(boolean debug) {
        settings.debug = debug;
    }

    public String trans(@Nonnull String key, Object... args) {
        String text = langMap.get(key);
        // fallback to Violet
        if (text == null || text.isEmpty()) text = Manager.trans(settings.lang, key);
        return (text == null || text.isEmpty()) ? key : args.length > 0 ? String.format(text, args) : text;
    }

    public void send(@Nonnull CommandSender sender, @Nonnull String format) {
        sender.sendMessage(colorHead + format.replace('&', COLOR_CHAR));
    }

    public void sendKey(@Nonnull CommandSender sender, @Nonnull String key, Object... args) {
        send(sender, trans(key, args));
    }

    public void broadcast(@Nonnull String format) {
        Bukkit.broadcastMessage(colorHead + format.replace('&', COLOR_CHAR));
    }

    public void broadcastKey(@Nonnull String key, Object... args) {
        broadcast(trans(key, args));
    }

    public void console(@Nonnull String format) {
        Bukkit.getConsoleSender().sendMessage(colorHead + format.replace('&', COLOR_CHAR));
    }

    public void consoleKey(@Nonnull String key, Object... args) {
        console(trans(key, args));
    }

    public void println(@Nonnull String plain) {
        System.out.println(plainHead + plain);
    }

    final HashMap<String, String> loadLangMap(@Nonnull String lang) {
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
            return langNode.asStringMap();
        } catch (Throwable e) {
            if (extract) console("&cLang file " + lang + " load exception !!!");
            else console("&cLang file " + lang + " extract exception !!!");
            if (settings.debug) e.printStackTrace();
            return new HashMap<>();
        }
    }
}
