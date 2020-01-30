package org.soraworld.violet.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.hocon.exception.SerializerException;
import org.soraworld.hocon.node.*;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.ICommandSender;
import org.soraworld.violet.api.IConfig;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.asm.ClassInfo;
import org.soraworld.violet.asm.PluginScanner;
import org.soraworld.violet.command.BaseCommands;
import org.soraworld.violet.command.CommandCore;
import org.soraworld.violet.inject.Command;
import org.soraworld.violet.inject.Config;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.inject.Injector;
import org.soraworld.violet.manager.Translator;
import org.soraworld.violet.serializers.UUIDSerializer;
import org.soraworld.violet.text.ChatColor;
import org.soraworld.violet.text.JsonText;
import org.soraworld.violet.util.FileUtils;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class PluginCore {

    /* ---------------------------- Settings ---------------------------- */
    @Setting
    protected String version = "0.0.0";
    @Setting
    protected String lang = Locale.CHINA.equals(Locale.getDefault()) ? "zh_cn" : "en_us";
    @Setting
    protected boolean debug = false;
    @Setting
    protected boolean autoUpLang = true;
    @Setting
    protected boolean autoBackUp = true;
    @Setting
    protected boolean saveOnDisable = true;

    /* ---------------------------- Temp Properties ---------------------------- */
    private String plainHead;
    private String colorHead;
    private JsonText jsonHead;
    private boolean reloadSuccess = false;
    private ChatColor color = ChatColor.WHITE;

    /* ---------------------------- Final Properties ---------------------------- */
    private final Path confile;
    private final Path rootPath;
    private final IPlugin plugin;
    private final FileNode rootNode;
    private final Set<ClassInfo> classes = new HashSet<>();
    private final Options options = Options.build();
    private final Injector injector = new Injector();
    private HashMap<String, String> langMap = new HashMap<>();
    private final HashMap<String, IConfig> configs = new HashMap<>();
    private final TreeMap<Integer, List<Consumer<ClassInfo>>> scannersMap = new TreeMap<>();
    private final AtomicBoolean asyncSaveLock = new AtomicBoolean(false);
    private final AtomicBoolean asyncBackLock = new AtomicBoolean(false);

    /* ---------------------------- Statics ---------------------------- */
    private static final ArrayList<IPlugin> PLUGINS = new ArrayList<>();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
    private static HashMap<String, HashMap<String, String>> langMaps = new HashMap<>();

    private static Translator translator;

    public PluginCore(@NotNull IPlugin plugin) {
        this.plugin = plugin;
        this.injector.addValue(this);
        this.injector.addValue(plugin);
        this.rootPath = plugin.getRootPath();
        this.classes.addAll(PluginScanner.scan(plugin.getJarFile()));
        this.options.setTranslator(Options.COMMENT, this::trans);
        this.options.setTranslator(Options.READ, ChatColor::colorize);
        this.options.setTranslator(Options.WRITE, ChatColor::fakerize);
        try {
            this.options.registerType(new UUIDSerializer());
        } catch (SerializerException e) {
            e.printStackTrace();
        }
        this.confile = rootPath.resolve(plugin.getId().replace(' ', '_') + ".conf");
        this.rootNode = new FileNode(confile.toFile(), options);
        setHead(defChatHead());
        if (!PLUGINS.contains(plugin)) {
            PLUGINS.add(plugin);
        }
        if (plugin.getId().equalsIgnoreCase(Violet.PLUGIN_ID)) {
            translator = (lang, key) -> langMaps.computeIfAbsent(lang, this::loadLangMap).get(key);
        }
    }

    public static void listPlugins(ICommandSender sender) {
        for (IPlugin plugin : PLUGINS) {
            sender.sendMessageKey("pluginInfo", plugin.getId(), plugin.getVersion());
        }
    }

    public void setHead(String head) {
        this.colorHead = color + ChatColor.colorize(head) + ChatColor.RESET;
        this.plainHead = ChatColor.stripColor(colorHead);
        this.jsonHead = new JsonText(colorHead);
    }

    final HashMap<String, String> loadLangMap(String lang) {
        Path langFile = rootPath.resolve("lang").resolve(lang + ".lang");
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
                plugin.console(ChatColor.RED + "Lang file " + langFile + " load exception !!!");
            } else {
                plugin.console(ChatColor.RED + "Lang file " + url + " extract exception !!!");
            }
            debug(e);
            return new HashMap<>();
        }
    }

    public boolean load() {
        if (Files.notExists(confile)) {
            setLang(lang);
            save();
            return true;
        }
        try {
            rootNode.load(true, true);
            Node general = rootNode.get("general");
            if (general instanceof NodeMap) {
                ((NodeMap) general).modify(this);
            }
            configs.forEach((id, config) -> {
                Node node = rootNode.get(id);
                if (node instanceof NodeMap) {
                    ((NodeMap) node).modify(config);
                }
            });
            if (!setLang(lang) && !"en_us".equalsIgnoreCase(lang)) {
                setLang("en_us");
            }
            setDebug(debug);
            reloadSuccess = true;
            if (!plugin.getVersion().equalsIgnoreCase(version)) {
                plugin.consoleKey("versionChanged", version, plugin.getVersion());
                if (autoBackUp) {
                    plugin.consoleKey(backup() ? "backupSuccess" : "backupFailed");
                }
                if (autoUpLang) {
                    plugin.consoleKey(extract() ? "reExtracted" : "reExtractFailed");
                }
            }
            return true;
        } catch (Throwable e) {
            plugin.console(ChatColor.RED + "Config file load exception !!!");
            e.printStackTrace();
            reloadSuccess = false;
            return false;
        }
    }

    public boolean save() {
        reloadSuccess = true;
        version = plugin.getVersion();
        try {
            NodeMap general = new NodeMap(options, trans("comment.type.general"));
            general.extract(this);
            rootNode.clear();
            rootNode.set("general", general);
            configs.forEach((id, config) -> {
                NodeMap node = new NodeMap(options, trans("comment.type." + id));
                node.extract(config);
                rootNode.set(id, node);
            });
            rootNode.save();
            return true;
        } catch (Throwable e) {
            plugin.console(ChatColor.RED + "Config file save exception !!!");
            e.printStackTrace();
            return false;
        }
    }

    public void asyncSave(@Nullable Consumer<Boolean> callback) {
        if (!asyncSaveLock.get()) {
            asyncSaveLock.set(true);
            plugin.runTaskAsync(() -> {
                boolean result = save();
                if (callback != null) {
                    plugin.runTask(() -> callback.accept(result));
                }
                asyncSaveLock.set(false);
            });
        } else {
            plugin.consoleKey("asyncSaveBlock");
        }
    }

    public boolean backup() {
        Path target = rootPath.resolve("backup/" + DATE_FORMAT.format(new Date()) + ".zip");
        return FileUtils.zipArchivePath(rootPath, target, p -> {
            String name = rootPath.relativize(p).toString().toLowerCase();
            return !name.startsWith("backup");
        });
    }

    public void asyncBackup(@Nullable Consumer<Boolean> callback) {
        if (!asyncBackLock.get()) {
            asyncBackLock.set(true);
            plugin.runTaskAsync(() -> {
                boolean flag = backup();
                if (callback != null) {
                    plugin.runTask(() -> callback.accept(flag));
                }
                asyncBackLock.set(false);
            });
        } else {
            plugin.consoleKey("asyncBackupBlock");
        }
    }

    public boolean extract() {
        if (FileUtils.deletePath(rootPath.resolve("lang").toFile(), debug)) {
            return setLang(lang);
        }
        if (debug) {
            plugin.console(ChatColor.RED + "deletePath " + rootPath.resolve("lang") + " failed !!");
        }
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
            if (head != null && !head.isEmpty()) {
                setHead(head);
            }
            return true;
        } else {
            plugin.consoleKey("emptyLangMap");
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
        String text = null;
        if (langMap.containsKey(key)) {
            text = langMap.get(key);
        } else if (!plugin.getId().equalsIgnoreCase(Violet.PLUGIN_ID) && translator != null) {
            text = translator.trans(lang, key);
        }
        if (text == null || text.isEmpty()) {
            return key;
        }
        if (args.length > 0) {
            try {
                return String.format(text, args);
            } catch (Throwable e) {
                plugin.console(ChatColor.RED + "Translation " + key + " -> " + text + " format failed !");
            }
        }
        return text;
    }

    public Path getRootPath() {
        return rootPath;
    }

    public boolean canSaveOnDisable() {
        return saveOnDisable && reloadSuccess;
    }

    public String defChatHead() {
        return "[" + plugin.getName() + "] ";
    }

    public String defAdminPerm() {
        return plugin.getId() + ".admin";
    }

    public void debugKey(String key, Object... args) {
        if (debug) {
            plugin.consoleKey(key, args);
        }
    }

    public void debug(String text) {
        if (debug) {
            plugin.console(text);
        }
    }

    public void debug(Throwable e) {
        if (debug) {
            e.printStackTrace();
        }
    }

    public void beforeLoad() {
    }

    public void afterLoad() {
    }

    public void addScanner(int priority, @NotNull Consumer<ClassInfo> scanner) {
        priority = Math.max(0, Math.min(10, priority));
        scannersMap.computeIfAbsent(priority, i -> new ArrayList<>()).add(scanner);
    }

    public void scan() {
        for (int i = 0; i <= 10; i++) {
            List<Consumer<ClassInfo>> scanners = scannersMap.get(i);
            if (scanners != null) {
                classes.forEach(clazz -> scanners.forEach(scanner -> scanner.accept(clazz)));
            }
        }
    }

    // 0 - 配置 (注入源/被注入)
    // 1 - 命令 (被注入)
    // 1 - 监听器 (被注入)
    private void addScanners(ClassLoader loader, Path rootPath) {
        addScanner(0, info -> {
            if (info.hasAnnotation(Config.class)) {
                plugin.console("Try construct @Config -> " + info.getName());
                try {
                    Class<?> clazz = Class.forName(info.getName(), false, loader);
                    if (clazz != null && IConfig.class.isAssignableFrom(clazz)) {
                        Config config = clazz.getAnnotation(Config.class);
                        if (configs.containsKey(config.id())) {
                            plugin.consoleKey("configIdExist", config.id());
                        } else if (!config.id().matches("[a-zA-Z_0-9]+")) {
                            plugin.consoleKey("illegalConfigId", config.id());
                        } else {
                            injector.inject(clazz);
                            Constructor<?> constructor = clazz.getConstructor();
                            constructor.setAccessible(true);
                            IConfig instance = (IConfig) constructor.newInstance();
                            injector.inject(instance);
                            injector.addValue(instance);
                            configs.put(config.id(), instance);
                            plugin.consoleKey("injectConfig", config.id());
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        addScanner(1, info -> {
            if (info.hasAnnotation(Command.class)) {
                plugin.console("Try construct @Command -> " + info.getName());
                try {
                    Class<?> clazz = Class.forName(info.getName(), false, loader);
                    Command annotation = clazz.getAnnotation(Command.class);
                    try {
                        Object instance = clazz.getConstructor().newInstance();
                        injector.inject(instance);
                        CommandCore core = new CommandCore(plugin, annotation);
                        if (plugin.registerCommand(core)) {
                            core.extractSub(instance);
                            core.extractTab(instance);
                            if (clazz != BaseCommands.class && core.getName().equalsIgnoreCase(plugin.getId())) {
                                BaseCommands base = new BaseCommands();
                                injector.inject(base);
                                core.extractSub(base);
                                core.extractTab(base);
                            }
                        } else {
                            plugin.consoleKey("commandRegisterFailed", core.getName());
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        addScanner(1, info -> {
            if (info.hasAnnotation(Inject.class)) {
                try {
                    Class<?> clazz = Class.forName(info.getName(), false, loader);
                    injector.inject(clazz);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onLoad() {
        Path rootPath = plugin.getRootPath();
        if (Files.notExists(rootPath)) {
            try {
                Files.createDirectories(rootPath);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        addScanners(plugin.getClass().getClassLoader(), rootPath);
        plugin.onPluginLoad();
    }

    public void onEnable() {
        scan();
        load();
        plugin.onPluginEnable();
        plugin.consoleKey("pluginEnabled", plugin.getId() + "-" + plugin.getVersion());
    }

    public void onDisable() {
        plugin.onPluginDisable();
        plugin.consoleKey("pluginDisabled", plugin.getId() + "-" + plugin.getVersion());
        if (saveOnDisable) {
            plugin.consoleKey(save() ? "configSaved" : "configSaveFailed");
        }
    }

    public String getChatHead() {
        return colorHead;
    }
}
