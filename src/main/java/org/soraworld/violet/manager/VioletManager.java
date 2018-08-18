package org.soraworld.violet.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Options;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.serializers.LocationSerializer;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;

import static org.soraworld.violet.Violets.*;

public abstract class VioletManager implements IManager {

    @Setting(comment = "comment.lang")
    protected String lang = "zh_cn";
    @Setting(comment = "comment.debug")
    protected boolean debug = false;

    protected final Path path;
    protected final Path confile;
    protected final IPlugin plugin;
    protected String plainHead;
    protected String colorHead;
    protected final Options options = Options.build();
    protected HashMap<String, String> langMap = new HashMap<>();

    public VioletManager(IPlugin plugin, Path path) {
        this.path = path;
        this.plugin = plugin;
        this.options.setTranslator(this::trans);
        this.options.registerType(new LocationSerializer());
        this.confile = path.resolve(plugin.getId().replace(' ', '_') + ".conf");
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
            rootNode.load(true);
            rootNode.modify(this);
            setLang(lang);
            options.setDebug(debug);
            return true;
        } catch (Throwable e) {
            console("&cConfig file load exception !!!");
            if (debug) e.printStackTrace();
            return false;
        }
    }

    public boolean save() {
        try {
            FileNode rootNode = new FileNode(confile.toFile(), options);
            rootNode.extract(this);
            rootNode.save();
            return true;
        } catch (Throwable e) {
            console("&cConfig file save exception !!!");
            if (debug) e.printStackTrace();
            return false;
        }
    }

    public String getLang() {
        return lang;
    }

    public void setLang(@Nonnull String lang) {
        this.lang = lang;
        HashMap<String, String> temp = loadLangMap(lang);
        if (!temp.isEmpty()) {
            langMap = temp;
            String head = langMap.get(KEY_CHAT_HEAD);
            if (head != null && !head.isEmpty()) setHead(head);
        } else consoleKey("emptyLangMap");
    }

    private void setHead(@Nonnull String head) {
        this.colorHead = defChatColor() + head.replace('&', COLOR_CHAR) + ChatColor.RESET;
        this.plainHead = COLOR_PATTERN.matcher(colorHead).replaceAll("");
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
        options.setDebug(debug);
    }

    public String trans(@Nonnull String key, Object... args) {
        String text = langMap.get(key);
        // fallback to Violet
        if (text == null || text.isEmpty()) text = Manager.trans(lang, key);
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
            langNode.load(true);
            return langNode.asStringMap();
        } catch (Throwable e) {
            if (extract) console("&cLang file " + lang + " load exception !!!");
            else console("&cLang file " + lang + " extract exception !!!");
            if (debug) e.printStackTrace();
            return new HashMap<>();
        }
    }
}
