package org.soraworld.violet.manager;

import org.jetbrains.annotations.NotNull;
import org.soraworld.hocon.exception.SerializerException;
import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Options;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.api.ISender;
import org.soraworld.violet.serializers.UUIDSerializer;
import org.soraworld.violet.text.JsonText;
import org.soraworld.violet.util.ChatColor;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 管理器抽象类.
 *
 * @param <T> 插件类型参数
 */
public abstract class VioletManager<T extends IPlugin> implements IManager {

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
     * 是否在插件停用时保存配置文件.
     */
    @Setting(comment = "comment.saveOnDisable")
    protected boolean saveOnDisable = true;
    /**
     * 被禁用的命令, 键: 主命令名; 值: 被禁用的子命令名列表.
     */
    @Setting(comment = "comment.disableCmds")
    protected HashMap<String, ArrayList<String>> disableCmds = new HashMap<>();

    /**
     * 纯文本抬头.
     */
    protected String plainHead;
    /**
     * 带颜色抬头.
     */
    protected String colorHead;
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
    protected static boolean asyncSaveLock = false;
    /**
     * 插件统计列表.
     */
    protected static final ArrayList<IPlugin> plugins = new ArrayList<>();
    protected static final String defLang = Locale.CHINA.equals(Locale.getDefault()) ? "zh_cn" : "en_us";
    static Translator translator = null;

    /**
     * 实例化管理器.
     *
     * @param plugin 插件实例
     * @param path   配置文件路径
     */
    public VioletManager(T plugin, Path path) {
        this.path = path;
        this.plugin = plugin;
        this.options.setTranslator(Options.COMMENT, this::trans);
        this.options.setTranslator(Options.READ, ChatColor::colorize);
        this.options.setTranslator(Options.WRITE, ChatColor::fakerize);
        try {
            this.options.registerType(new UUIDSerializer());
        } catch (SerializerException e) {
            console(ChatColor.RED + "TypeSerializer for UUID register failed");
            if (debug) e.printStackTrace();
        }
        this.confile = path.resolve(plugin.getId().replace(' ', '_') + ".conf");
        this.rootNode = new FileNode(confile.toFile(), options);
        setHead(defChatHead());
        if (!plugins.contains(plugin)) plugins.add(plugin);
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
     * @return 翻译映射表
     */
    final HashMap<String, String> loadLangMap(String lang) {
        Path langFile = path.resolve("lang").resolve(lang + ".lang");
        boolean extract = false;
        URL url = plugin.getAssetURL("lang/" + lang + ".lang");
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
            if (extract) console(ChatColor.RED + "Lang file " + langFile + " load exception !!!");
            else console(ChatColor.RED + "Lang file " + url + " extract exception !!!");
            debug(e);
            return new HashMap<>();
        }
    }

    public boolean load() {
        if (Files.notExists(confile)) {
            setLang(defLang);
            save();
            return true;
        }
        try {
            rootNode.load(true, true);
            rootNode.modify(this);
            boolean flag;
            if (autoUpLang && !plugin.getVersion().equalsIgnoreCase(version)) {
                consoleKey("versionChanged", version);
                flag = reExtract();
            } else flag = setLang(lang);
            if (!flag && !defLang.equalsIgnoreCase(lang)) setLang(defLang);
            options.setDebug(debug);
            reloadSuccess = true;
            return true;
        } catch (Throwable e) {
            console(ChatColor.RED + "Config file load exception !!!");
            debug(e);
            reloadSuccess = false;
            return false;
        }
    }

    public boolean save() {
        reloadSuccess = true;
        version = getPlugin().getVersion();
        try {
            rootNode.extract(this);
            rootNode.save();
            return true;
        } catch (Throwable e) {
            console(ChatColor.RED + "Config file save exception !!!");
            debug(e);
            return false;
        }
    }

    public boolean reExtract() {
        if (deletePath(path.resolve("lang").toFile())) return setLang(lang);
        if (debug) console(ChatColor.RED + "deletePath " + path.resolve("lang") + " failed !!");
        return false;
    }

    public String getLang() {
        return lang;
    }

    public boolean setLang(String lang) {
        lang = lang.toLowerCase();
        HashMap<String, String> temp = loadLangMap(lang);
        if (!temp.isEmpty()) {
            this.lang = lang;
            langMap = temp;
            String head = langMap.get("chatHead");
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

    public String trans(@NotNull String key, Object... args) {
        String text = langMap.get(key);
        if ((text == null || text.isEmpty()) && !plugin.getId().equalsIgnoreCase(Violet.PLUGIN_ID) && translator != null) {
            text = translator.trans(lang, key, args);
        }
        if (text == null || text.isEmpty()) return key;
        if (args.length > 0) {
            try {
                return String.format(text, args);
            } catch (Throwable e) {
                console(ChatColor.RED + "Translation " + key + " -> " + text + " format failed !");
            }
        }
        return text;
    }

    public void broadcastKey(String key, Object... args) {
        broadcast(trans(key, args));
    }

    public void consoleKey(String key, Object... args) {
        console(trans(key, args));
    }

    public void println(String text) {
        System.out.println(plainHead + text);
    }

    public T getPlugin() {
        return plugin;
    }

    public Path getPath() {
        return path;
    }

    /**
     * 在停用插件时是否可以保存配置.
     *
     * @return 是否可以保存配置
     */
    public boolean canSaveOnDisable() {
        return saveOnDisable && reloadSuccess;
    }

    /**
     * 获取主命令下被禁用的子命令名列表.
     *
     * @param name 主命令名
     * @return 被禁用的子命令名列表
     */
    public ArrayList<String> getDisableCmds(String name) {
        return disableCmds == null ? new ArrayList<>() : disableCmds.getOrDefault(name, new ArrayList<>());
    }

    /**
     * 删除路径下文件,文件夹及自身.
     *
     * @param path 路径
     * @return 是否全部成功
     */
    public boolean deletePath(File path) {
        if (path.isFile()) {
            boolean flag = path.delete();
            if (debug && !flag) console(ChatColor.RED + "File " + path + " delete failed !!");
            return flag;
        }
        File[] files = path.listFiles();
        boolean flag = true;
        if (files != null && files.length > 0) {
            for (File file : files) flag = flag && deletePath(file);
        } else {
            flag = path.delete();
        }
        return flag;
    }

    public void listPlugins(ISender sender) {
        for (IPlugin plugin : plugins) {
            sendKey(sender, "pluginInfo", plugin.getId(), plugin.getVersion());
        }
    }
}
