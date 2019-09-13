package org.soraworld.violet.manager;

import org.jetbrains.annotations.NotNull;
import org.soraworld.hocon.exception.SerializerException;
import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Options;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.serializers.UUIDSerializer;
import org.soraworld.violet.text.JsonText;
import org.soraworld.violet.util.ChatColor;
import org.soraworld.violet.util.FileUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The type Manager.
 *
 * @param <T> the type parameter
 * @author Himmelt
 */
public abstract class IManager<T extends IPlugin> {

    /**
     * 插件版本, 用于自动任务.
     */
    @Setting(comment = "comment.version")
    protected String version = "0.0.0";
    /**
     * 语言设置项.
     */
    @Setting(comment = "comment.lang")
    protected String lang = "zh_cn";
    /**
     * 调试设置项.
     */
    @Setting(comment = "comment.debug")
    protected boolean debug = false;
    /**
     * 是否在版本变化时自动释放更新语言文件.
     */
    @Setting(comment = "comment.autoUpLang")
    protected boolean autoUpLang = true;
    /**
     * 是否在版本变化时自动备份配置文件夹.
     */
    @Setting(comment = "comment.autoBackUp")
    protected boolean autoBackUp = true;
    /**
     * 是否检查更新
     */
    @Setting(comment = "comment.checkUpdate")
    protected boolean checkUpdate = true;
    /**
     * 是否在插件停用时保存配置文件.
     */
    @Setting(comment = "comment.saveOnDisable")
    protected boolean saveOnDisable = true;
    /**
     * The Perm map.
     */
    @Setting(comment = "comment.permMap")
    protected HashMap<String, String> permMap = new HashMap<>();

    /**
     * 纯文本抬头.
     */
    protected String plainHead;
    /**
     * 带颜色抬头.
     */
    protected String colorHead;
    /**
     * The Json head.
     */
    protected JsonText jsonHead;
    /**
     * 配置是否加载成功.
     */
    protected boolean reloadSuccess = false;
    /**
     * 配置保存路径.
     */
    protected final Path path;
    /**
     * 配置文件.
     */
    protected final Path confile;
    /**
     * 配置文件根节点.
     */
    protected final FileNode rootNode;
    /**
     * 插件实例.
     */
    protected final T plugin;
    /**
     * Hocon 配置选项.
     */
    protected final Options options = Options.build();
    /**
     * 语言翻译映射表.
     */
    protected HashMap<String, String> langMap = new HashMap<>();
    /**
     * 异步锁.
     */
    protected AtomicBoolean asyncSaveLock = new AtomicBoolean(false);
    /**
     * The Async back lock.
     */
    protected AtomicBoolean asyncBackLock = new AtomicBoolean(false);
    /**
     * 插件统计列表.
     */
    protected static final ArrayList<IPlugin> PLUGINS = new ArrayList<>();
    /**
     * The constant DEF_LANG.
     */
    protected static final String DEF_LANG = Locale.CHINA.equals(Locale.getDefault()) ? "zh_cn" : "en_us";
    /**
     * The Translator.
     */
    static Translator translator = null;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");

    /**
     * 实例化管理器.
     *
     * @param plugin 插件实例
     * @param path   配置文件路径
     */
    public IManager(T plugin, Path path) {
        this.path = path;
        this.plugin = plugin;
        this.options.setTranslator(Options.COMMENT, this::trans);
        this.options.setTranslator(Options.READ, ChatColor::colorize);
        this.options.setTranslator(Options.WRITE, ChatColor::fakerize);
        try {
            this.options.registerType(new UUIDSerializer());
        } catch (SerializerException e) {
            console(ChatColor.RED + "TypeSerializer for UUID register failed");
            if (debug) {
                e.printStackTrace();
            }
        }
        this.confile = path.resolve(plugin.getId().replace(' ', '_') + ".conf");
        this.rootNode = new FileNode(confile.toFile(), options);
        setHead(defChatHead());
        if (!PLUGINS.contains(plugin)) {
            PLUGINS.add(plugin);
        }
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
     * 获取对应语言的翻译映射表.
     *
     * @param lang 目标语言
     * @return 翻译映射表 hash map
     */
    final HashMap<String, String> loadLangMap(String lang) {
        Path langFile = path.resolve("lang").resolve(lang + ".lang");
        boolean extract = false;
        URL url = plugin.getAssetUrl("lang/" + lang + ".lang");
        try {
            if (Files.notExists(langFile)) {
                Files.createDirectories(langFile.getParent());
                Files.copy(url.openStream(), langFile);
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
            if (extract) {
                console(ChatColor.RED + "Lang file " + langFile + " load exception !!!");
            } else {
                console(ChatColor.RED + "Lang file " + url + " extract exception !!!");
            }
            debug(e);
            return new HashMap<>();
        }
    }

    /**
     * Load boolean.
     *
     * @return the boolean
     */
    public boolean load() {
        if (Files.notExists(confile)) {
            setLang(DEF_LANG);
            save();
            return true;
        }
        try {
            rootNode.load(true, true);
            rootNode.modify(this);
            permMap.putIfAbsent("admin", defAdminPerm());
            if (!setLang(lang) && !DEF_LANG.equalsIgnoreCase(lang)) {
                setLang(DEF_LANG);
            }
            options.setDebug(debug);
            reloadSuccess = true;
            if (!plugin.getVersion().equalsIgnoreCase(version)) {
                consoleKey("versionChanged", version, plugin.getVersion());
                if (autoBackUp) {
                    consoleKey(doBackUp() ? "backUpSuccess" : "backUpFailed");
                }
                if (autoUpLang) {
                    consoleKey(reExtract() ? "reExtracted" : "reExtractFailed");
                }
            }
            return true;
        } catch (Throwable e) {
            console(ChatColor.RED + "Config file load exception !!!");
            debug(e);
            reloadSuccess = false;
            return false;
        }
    }

    /**
     * Save boolean.
     *
     * @return the boolean
     */
    public boolean save() {
        reloadSuccess = true;
        version = getPlugin().getVersion();
        try {
            permMap.putIfAbsent("admin", defAdminPerm());
            rootNode.extract(this);
            rootNode.save();
            return true;
        } catch (Throwable e) {
            console(ChatColor.RED + "Config file save exception !!!");
            debug(e);
            return false;
        }
    }

    /**
     * Do back up boolean.
     *
     * @return the boolean
     */
    public boolean doBackUp() {
        Path target = path.resolve("backup/" + DATE_FORMAT.format(new Date()) + ".zip");
        return FileUtils.zipArchivePath(path, target, p -> !path.relativize(p).toString().toLowerCase().startsWith("backup"));
    }

    /**
     * Re extract boolean.
     *
     * @return the boolean
     */
    public boolean reExtract() {
        if (FileUtils.deletePath(path.resolve("lang").toFile(), debug)) {
            return setLang(lang);
        }
        if (debug) {
            console(ChatColor.RED + "deletePath " + path.resolve("lang") + " failed !!");
        }
        return false;
    }

    /**
     * Gets lang.
     *
     * @return the lang
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets lang.
     *
     * @param lang the lang
     * @return the lang
     */
    public boolean setLang(String lang) {
        lang = lang.toLowerCase();
        HashMap<String, String> temp = loadLangMap(lang);
        if (!temp.isEmpty()) {
            this.lang = lang;
            langMap = temp;
            String head = langMap.get("chatHead");
            if (head != null && !head.isEmpty()) {
                setHead(head);
            }
            return true;
        } else {
            consoleKey("emptyLangMap");
            return false;
        }
    }

    /**
     * Is debug boolean.
     *
     * @return the boolean
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Sets debug.
     *
     * @param debug the debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
        options.setDebug(debug);
    }

    /**
     * Trans string.
     *
     * @param key  the key
     * @param args the args
     * @return the string
     */
    public String trans(@NotNull String key, Object... args) {
        String text = langMap.get(key);
        if ((text == null || text.isEmpty()) && !plugin.getId().equalsIgnoreCase(Violet.PLUGIN_ID) && translator != null) {
            text = translator.trans(lang, key, args);
        }
        if (text == null || text.isEmpty()) {
            return key;
        }
        if (args.length > 0) {
            try {
                return String.format(text, args);
            } catch (Throwable e) {
                console(ChatColor.RED + "Translation " + key + " -> " + text + " format failed !");
            }
        }
        return text;
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

    /**
     * Println.
     *
     * @param text the text
     */
    public void println(String text) {
        System.out.println(plainHead + text);
    }

    /**
     * Gets plugin.
     *
     * @return the plugin
     */
    public T getPlugin() {
        return plugin;
    }

    /**
     * Gets path.
     *
     * @return the path
     */
    public Path getPath() {
        return path;
    }

    /**
     * 在停用插件时是否可以保存配置.
     *
     * @return 是否可以保存配置 boolean
     */
    public boolean canSaveOnDisable() {
        return saveOnDisable && reloadSuccess;
    }

    /*------------------------------------------------*/

    /**
     * Def chat head string.
     *
     * @return the string
     */
    public String defChatHead() {
        return "[" + getPlugin().getName() + "] ";
    }

    /**
     * Def admin perm string.
     *
     * @return the string
     */
    public String defAdminPerm() {
        return getPlugin().getId() + ".admin";
    }

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
     * Before load.
     */
    public void beforeLoad() {
    }

    /**
     * After load.
     */
    public void afterLoad() {
    }

    /**
     * Has update boolean.
     *
     * @return the boolean
     */
    public boolean hasUpdate() {
        try {
            URL url = new URL(plugin.updateUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            String text = conn.getHeaderField("Location");
            text = text.substring(text.lastIndexOf('/'));
            if (!text.matches("/v\\d\\.\\d\\.\\d")) {
                return false;
            }
            return !text.contains(plugin.getVersion());
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * Mapping perm string.
     *
     * @param perm the perm
     * @return the string
     */
    public String mappingPerm(String perm) {
        return permMap.getOrDefault(perm, perm);
    }

    /**
     * Def chat color chat color.
     *
     * @return the chat color
     */
    public abstract ChatColor defChatColor();

    /**
     * Console.
     *
     * @param text the text
     */
    public abstract void console(String text);

    /**
     * Broadcast.
     *
     * @param message the message
     */
    public abstract void broadcast(String message);
}
