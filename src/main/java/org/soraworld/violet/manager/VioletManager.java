package org.soraworld.violet.manager;

import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Options;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.util.ChatColor;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class VioletManager implements IManager {

    @Setting(comment = "comment.lang")
    protected String lang = "zh_cn";
    @Setting(comment = "comment.debug")
    protected boolean debug = false;

    protected String plainHead;
    protected String colorHead;
    protected final Path path;
    protected final Path confile;
    protected final IPlugin plugin;
    protected final Options options = Options.build();
    protected HashMap<String, String> langMap = new HashMap<>();
    protected static final ArrayList<IPlugin> plugins = new ArrayList<>();

    public VioletManager(@Nonnull IPlugin plugin, @Nonnull Path path) {
        this.path = path;
        this.plugin = plugin;
        this.options.setTranslator(this::trans);
        this.confile = path.resolve(plugin.getId().replace(' ', '_') + ".conf");
        setHead(defChatHead());
        if (!plugins.contains(plugin)) plugins.add(plugin);
    }

    private void setHead(@Nonnull String head) {
        this.colorHead = defChatColor() + ChatColor.colorize(head) + ChatColor.RESET;
        this.plainHead = ChatColor.REAL_COLOR.matcher(colorHead).replaceAll("");
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
            HashMap<String, String> map = langNode.asStringMap();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                entry.setValue(ChatColor.colorize(entry.getValue()));
            }
            return map;
        } catch (Throwable e) {
            if (extract) console(ChatColor.RED + "Lang file " + lang + " load exception !!!");
            else console(ChatColor.RED + "Lang file " + lang + " extract exception !!!");
            if (debug) e.printStackTrace();
            return new HashMap<>();
        }
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
            if (!setLang(lang)) {
                setLang(Locale.CHINA.equals(Locale.getDefault()) ? "zh_cn" : "en_us");
            }
            options.setDebug(debug);
            return true;
        } catch (Throwable e) {
            console(ChatColor.RED + "Config file load exception !!!");
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
            console(ChatColor.RED + "Config file save exception !!!");
            if (debug) e.printStackTrace();
            return false;
        }
    }

    public String getLang() {
        return lang;
    }

    public boolean setLang(@Nonnull String lang) {
        HashMap<String, String> temp = loadLangMap(lang);
        if (!temp.isEmpty()) {
            this.lang = lang;
            langMap = temp;
            String head = langMap.get(Violet.KEY_CHAT_HEAD);
            if (head != null && !head.isEmpty()) setHead(head);
            return true;
        } else {
            consoleKey("emptyLangMap");
            return false;
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
        options.setDebug(debug);
    }

    public void broadcastKey(@Nonnull String key, Object... args) {
        broadcast(trans(key, args));
    }

    public void consoleKey(@Nonnull String key, Object... args) {
        console(trans(key, args));
    }

    public void println(@Nonnull String text) {
        System.out.println(plainHead + text);
    }
}
