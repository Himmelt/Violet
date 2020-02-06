package org.soraworld.violet.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.hocon.node.*;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.ICommandSender;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.asm.ClassInfo;
import org.soraworld.violet.asm.PluginScanner;
import org.soraworld.violet.bstats.Metrics;
import org.soraworld.violet.command.BaseCommands;
import org.soraworld.violet.command.CommandCore;
import org.soraworld.violet.inject.Cmd;
import org.soraworld.violet.inject.Config;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.log.Logger;
import org.soraworld.violet.serializers.UUIDSerializer;
import org.soraworld.violet.text.ChatColor;
import org.soraworld.violet.text.JsonText;
import org.soraworld.violet.util.FileUtils;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static org.soraworld.violet.Violet.MC_VERSION;
import static org.soraworld.violet.Violet.SERVER_UUID;

public final class PluginCore {

    /* ---------------------------- Settings ---------------------------- */
    @Setting
    private String version = "0.0.0";
    @Setting
    private String lang = Locale.CHINA.equals(Locale.getDefault()) ? "zh_cn" : "en_us";
    @Setting
    private boolean debug = false;
    @Setting
    private boolean autoUpLang = true;
    @Setting
    private boolean autoBackUp = true;
    @Setting
    private boolean saveOnDisable = true;
    @Setting
    private final HashSet<String> backupExcludes = new HashSet<>();

    /* ---------------------------- Temp Properties ---------------------------- */
    private String plainHead;
    private String colorHead;
    private JsonText jsonHead;
    private boolean reloadSuccess = false;
    private ChatColor color = ChatColor.WHITE;

    /* ---------------------------- Final Properties ---------------------------- */
    private final Path rootPath;
    private final IPlugin plugin;
    private final Options options = Options.build();
    private final Injector injector = new Injector();
    private final HashMap<String, String> langMap = new HashMap<>();
    private final HashMap<String, Object> internalConfigs = new HashMap<>();
    private final HashMap<String, Object> externalConfigs = new HashMap<>();
    private final CopyOnWriteArrayList<Runnable> enableActions = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Runnable> disableActions = new CopyOnWriteArrayList<>();
    private final TreeMap<Integer, List<Consumer<ClassInfo>>> scannersMap = new TreeMap<>();
    private final AtomicBoolean asyncSaveLock = new AtomicBoolean(false);
    private final AtomicBoolean asyncBackLock = new AtomicBoolean(false);

    private final Logger logger;

    /* ---------------------------- Statics ---------------------------- */
    private static final ArrayList<IPlugin> PLUGINS = new ArrayList<>();
    private static final Pattern CONFIG_ID = Pattern.compile("[a-zA-Z_0-9]+");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
    private static HashMap<String, HashMap<String, String>> langMaps = new HashMap<>();
    private static BiFunction<String, String, String> translator;

    public PluginCore(@NotNull IPlugin plugin) {
        this.plugin = plugin;
        this.injector.addValue(this);
        this.injector.addValue(plugin);
        this.rootPath = plugin.getRootPath();
        this.logger = new Logger(rootPath.resolve("logs"));
        this.options.registerType(new UUIDSerializer());
        this.options.setTranslator(Options.COMMENT, this::trans);
        this.options.setTranslator(Options.READ, ChatColor::colorize);
        this.options.setTranslator(Options.WRITE, ChatColor::fakerize);
        setHead(defChatHead());
        if (!PLUGINS.contains(plugin)) {
            PLUGINS.add(plugin);
        }
        if (plugin.id().equalsIgnoreCase(Violet.PLUGIN_ID)) {
            addEnableAction(() -> {
                if (Violet.enableStats()) {
                    new Metrics(plugin);
                }
            });
            translator = (lang, key) -> langMaps.computeIfAbsent(lang, this::loadLangMap).get(key);
        }
    }

    public static void listPlugins(ICommandSender sender) {
        for (IPlugin plugin : PLUGINS) {
            sender.sendMessageKey("pluginInfo", plugin.id(), plugin.version());
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
        Path confile = rootPath.resolve(plugin.id() + ".conf");
        if (Files.notExists(confile)) {
            setLang(lang);
            save();
            return true;
        }
        try {
            FileNode rootNode = new FileNode(confile.toFile(), options);
            // TODO keepcomments 是否无效了，由于重新构建了FileNode
            rootNode.load(true, true);
            Node general = rootNode.get("general");
            if (general instanceof NodeMap) {
                ((NodeMap) general).modify(this);
            }
            internalConfigs.forEach((id, config) -> {
                Node node = rootNode.get(id);
                if (node instanceof NodeMap) {
                    ((NodeMap) node).modify(config);
                }
            });
            // TODO
            externalConfigs.forEach((id, config) -> {

            });
            if (!setLang(lang) && !"en_us".equalsIgnoreCase(lang)) {
                setLang("en_us");
            }
            setDebug(debug);
            reloadSuccess = true;
            if (!plugin.version().equalsIgnoreCase(version)) {
                plugin.consoleKey("versionChanged", version, plugin.version());
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
        version = plugin.version();
        try {
            Path confile = rootPath.resolve(plugin.id() + ".conf");
            FileNode rootNode = new FileNode(confile.toFile(), options);
            NodeMap general = new NodeMap(options, trans("comment.type.general"));
            general.extract(this);
            rootNode.set("general", general);
            internalConfigs.forEach((id, config) -> {
                NodeMap node = new NodeMap(options, trans("comment.type." + id));
                node.extract(config);
                rootNode.set(id, node);
            });
            rootNode.save();
            externalConfigs.forEach((id, config) -> {
                if (!id.equalsIgnoreCase(plugin.id())) {
                    try {
                        FileNode node = new FileNode(rootPath.resolve(id + ".conf").toFile(), options);
                        node.addHead(trans("comment.type." + id));
                        node.extract(config);
                        node.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    plugin.consoleKey("configIdEqualsPluginId", id);
                }
            });
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
        Path target = rootPath.resolve("backup").resolve(DATE_FORMAT.format(LocalDateTime.now()) + ".zip");
        return FileUtils.zipArchivePath(rootPath, target, p -> {
            String path = rootPath.relativize(p).toString().toLowerCase();
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            return !"backup".equals(path) && !backupExcludes.contains(path);
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
            langMap.clear();
            langMap.putAll(temp);
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
        } else if (!plugin.id().equalsIgnoreCase(Violet.PLUGIN_ID) && translator != null) {
            text = translator.apply(lang, key);
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

    public String defChatHead() {
        return "[" + plugin.name() + "] ";
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

    public void log(String text) {
        this.logger.log(text);
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
        Set<ClassInfo> classes = PluginScanner.scan(plugin.getJarFile());
        for (int i = 0; i <= 10; i++) {
            List<Consumer<ClassInfo>> scanners = scannersMap.get(i);
            if (scanners != null) {
                classes.forEach(clazz -> scanners.forEach(scanner -> scanner.accept(clazz)));
            }
        }
    }

    private void addScanners(ClassLoader loader, Path rootPath) {
        addScanner(0, info -> {
            if (info.hasAnnotation(Config.class)) {
                plugin.console("Try construct @Config -> " + info.getName());
                try {
                    Class<?> clazz = Class.forName(info.getName(), false, loader);
                    Config config = clazz.getAnnotation(Config.class);
                    String id = config.id();
                    if (CONFIG_ID.matcher(id).matches()) {
                        if (config.separate()) {
                            if (id.equalsIgnoreCase(plugin.id())) {
                                plugin.consoleKey("configIdEqualsPluginId", id);
                            } else if (externalConfigs.containsKey(id)) {
                                plugin.consoleKey("externalConfigIdExist", id);
                            } else {
                                injector.inject(clazz);
                                if (!config.clazz()) {
                                    Object instance = clazz.newInstance();
                                    injector.inject(instance);
                                    injector.addValue(instance);
                                    externalConfigs.put(id, instance);
                                } else {
                                    injector.addValue(clazz);
                                    externalConfigs.put(id, clazz);
                                }
                                plugin.consoleKey("injectExternalConfig", id);
                            }
                        } else {
                            if (internalConfigs.containsKey(id)) {
                                plugin.consoleKey("internalConfigIdExist", id);
                            } else {
                                injector.inject(clazz);
                                if (!config.clazz()) {
                                    Object instance = clazz.newInstance();
                                    injector.inject(instance);
                                    injector.addValue(instance);
                                    internalConfigs.put(id, instance);
                                } else {
                                    injector.addValue(clazz);
                                    internalConfigs.put(id, clazz);
                                }
                                plugin.consoleKey("injectInternalConfig", id);
                            }
                        }
                    } else {
                        plugin.consoleKey("illegalConfigId", id);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        addScanner(1, info -> {
            if (info.hasAnnotation(Cmd.class)) {
                plugin.console("Try construct @Cmd -> " + info.getName());
                try {
                    Class<?> clazz = Class.forName(info.getName(), false, loader);
                    Cmd cmd = clazz.getAnnotation(Cmd.class);
                    try {
                        Object instance = clazz.newInstance();
                        injector.inject(instance);
                        CommandCore core = new CommandCore(plugin, cmd);
                        if (plugin.registerCommand(core)) {
                            core.extractSub(instance);
                            core.extractTab(instance);
                            if (clazz != BaseCommands.class && core.getName().equalsIgnoreCase(plugin.id())) {
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
        enableActions.forEach(Runnable::run);
        plugin.consoleKey("pluginEnabled", plugin.id() + "-" + plugin.version());
        if (plugin.id().equalsIgnoreCase(Violet.PLUGIN_ID)) {
            plugin.consoleLogKey("serverRunning", SERVER_UUID, MC_VERSION);
        }
    }

    public void onDisable() {
        plugin.onPluginDisable();
        disableActions.forEach(Runnable::run);
        plugin.consoleKey("pluginDisabled", plugin.id() + "-" + plugin.version());
        if (saveOnDisable && reloadSuccess) {
            plugin.consoleKey(save() ? "configSaved" : "configSaveFailed");
        }
    }

    public void addEnableAction(@NotNull Runnable action) {
        enableActions.add(action);
    }

    public void addDisableAction(@NotNull Runnable action) {
        disableActions.add(action);
    }

    public String getChatHead() {
        return colorHead;
    }

    public static IPlugin getPlugin(@NotNull String id) {
        return PLUGINS.stream().filter(p -> p.id().equals(id)).findAny().orElse(null);
    }

    public static List<IPlugin> getPlugins() {
        return new ArrayList<>(PLUGINS);
    }
}
