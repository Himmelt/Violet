package org.soraworld.violet.manager;

import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Options;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.serializers.UUIDSerializer;
import org.soraworld.violet.util.ChatColor;

import javax.annotation.Nonnull;
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
     * 纯文本抬头.
     */
    protected String plainHead;
    /**
     * 带颜色抬头.
     */
    protected String colorHead;
    /**
     * 异步锁.
     */
    protected boolean asyncLock = false;
    /**
     * 配置保存路径.
     */
    protected final Path path;
    /**
     * 配置文件.
     */
    protected final Path confile;
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
     * 插件统计列表.
     */
    protected static final ArrayList<IPlugin> plugins = new ArrayList<>();

    /**
     * 实例化管理器.
     *
     * @param plugin 插件实例
     * @param path   配置文件路径
     */
    public VioletManager(@Nonnull T plugin, @Nonnull Path path) {
        this.path = path;
        this.plugin = plugin;
        this.options.setTranslator(this::trans);
        options.registerType(new UUIDSerializer());
        this.confile = path.resolve(plugin.getId().replace(' ', '_') + ".conf");
        setHead(defChatHead());
        if (!plugins.contains(plugin)) plugins.add(plugin);
    }

    private void setHead(@Nonnull String head) {
        this.colorHead = defChatColor() + ChatColor.colorize(head) + ChatColor.RESET;
        this.plainHead = ChatColor.REAL_COLOR.matcher(colorHead).replaceAll("");
    }

    /**
     * 获取对应语言的翻译映射表.
     *
     * @param lang 目标语言
     * @return 翻译映射表
     */
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

    /**
     * 异步保存配置.
     */
    public abstract void asyncSave();

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

    public T getPlugin() {
        return plugin;
    }

    public static int pluginsSize() {
        return plugins.size();
    }

    public static IPlugin getPluginAt(int index) {
        if (index >= 0 && index < plugins.size()) return plugins.get(index);
        return null;
    }
}
