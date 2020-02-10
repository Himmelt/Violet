package org.soraworld.violet.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.hocon.node.*;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.ICommandSender;
import org.soraworld.violet.api.IConfig;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.asm.ClassInfo;
import org.soraworld.violet.asm.PluginScanner;
import org.soraworld.violet.bstats.Metrics;
import org.soraworld.violet.command.BaseCommands;
import org.soraworld.violet.command.CommandCore;
import org.soraworld.violet.inject.Cmd;
import org.soraworld.violet.inject.Config;
import org.soraworld.violet.inject.Inject;
import org.soraworld.violet.inject.InjectListener;
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
import static org.soraworld.violet.Violet.getServerId;

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
    private CommandCore mainCommand;

    /* ---------------------------- Final Properties ---------------------------- */
    private final Path rootPath;
    private final Logger logger;
    private final IPlugin plugin;
    private final Options options = Options.build();
    private final Injector injector = new Injector();
    private final ChatColor color;
    private final HashMap<String, String> langMap = new HashMap<>();
    private final ArrayList<Object> listeners = new ArrayList<>();
    private final ArrayList<CommandCore> commands = new ArrayList<>();
    private final HashMap<String, Object> internalConfigs = new HashMap<>();
    private final HashMap<String, Object> externalConfigs = new HashMap<>();
    private final CopyOnWriteArrayList<Runnable> enableActions = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Runnable> disableActions = new CopyOnWriteArrayList<>();
    private final AtomicBoolean asyncSaveLock = new AtomicBoolean(false);
    private final AtomicBoolean asyncBackLock = new AtomicBoolean(false);

    /* ---------------------------- Statics ---------------------------- */
    private static final LinkedHashMap<String, IPlugin> PLUGINS = new LinkedHashMap<>();
    private static final Pattern CONFIG_ID = Pattern.compile("[a-zA-Z_0-9]+");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
    private static HashMap<String, HashMap<String, String>> langMaps = new HashMap<>();
    private static BiFunction<String, String, String> translator;

    public PluginCore(@NotNull IPlugin plugin) {
        this.plugin = plugin;
        this.color = plugin.chatColor();
        this.setHead("[" + plugin.name() + "] ");
        this.injector.addValue(this);
        this.injector.addValue(plugin);
        this.rootPath = plugin.getRootPath();
        this.logger = new Logger(plugin.id(), rootPath.resolve("logs"));
        this.addDisableAction(logger::shutdown);
        this.injector.addValue(this.logger);
        this.options.registerType(new UUIDSerializer());
        this.options.setUseDefaultCommentKey(true);
        this.options.setTranslator(Options.COMMENT, this::trans);
        this.options.setTranslator(Options.READ, ChatColor::colorize);
        this.options.setTranslator(Options.WRITE, ChatColor::fakerize);
        if (!PLUGINS.containsKey(plugin.id())) {
            PLUGINS.put(plugin.id(), plugin);
        }
        if (plugin.id().equalsIgnoreCase(Violet.PLUGIN_ID)) {
            addEnableAction(() -> {
                if (Violet.enableStats()) {
                    Metrics metrics = new Metrics(plugin);
                    metrics.start();
                    this.addDisableAction(metrics::shutdown);
                }
            });
            translator = (lang, key) -> langMaps.computeIfAbsent(lang, this::loadLangMap).get(key);
        }
    }

    public static void listPlugins(ICommandSender sender) {
        for (IPlugin plugin : PLUGINS.values()) {
            plugin.sendMessageKey(sender, "pluginInfo", plugin.id(), plugin.version());
        }
    }

    public void setHead(@NotNull String head) {
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
            rootNode.load();
            Node general = rootNode.get("general");
            if (general instanceof NodeMap) {
                ((NodeMap) general).modify(this);
            }
            setDebug(debug);
            if (!setLang(lang) && !"en_us".equalsIgnoreCase(lang)) {
                setLang("en_us");
            }
            internalConfigs.forEach((id, config) -> {
                Node node = rootNode.get(id);
                if (node instanceof NodeMap) {
                    ((NodeMap) node).modify(config);
                    if (config instanceof IConfig) {
                        ((IConfig) config).afterLoad();
                    }
                }
            });
            externalConfigs.forEach((id, config) -> {
                if (!id.equalsIgnoreCase(plugin.id())) {
                    try {
                        FileNode node = new FileNode(rootPath.resolve(id + ".conf").toFile(), options);
                        node.load();
                        node.modify(config);
                        if (config instanceof IConfig) {
                            ((IConfig) config).afterLoad();
                        }
                    } catch (Exception e) {
                        plugin.consoleKey("externalLoadFailed", id);
                        debug(e);
                    }
                } else {
                    plugin.consoleKey("configIdEqualsPluginId", id);
                }
            });
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
                if (config instanceof IConfig) {
                    ((IConfig) config).beforeSave();
                }
                node.extract(config);
                rootNode.set(id, node);
            });
            rootNode.save();
            externalConfigs.forEach((id, config) -> {
                if (!id.equalsIgnoreCase(plugin.id())) {
                    try {
                        FileNode node = new FileNode(rootPath.resolve(id + ".conf").toFile(), options);
                        node.addHead(trans("comment.type." + id));
                        if (config instanceof IConfig) {
                            ((IConfig) config).beforeSave();
                        }
                        node.extract(config);
                        node.save();
                    } catch (Exception e) {
                        plugin.consoleKey("externalSaveFailed", id);
                        debug(e);
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

    private @NotNull Consumer<ClassInfo> configScanner() {
        return info -> {
            if (info.hasAnnotation(Config.class)) {
                plugin.console("Try inject config -> " + info.getName());
                try {
                    Class<?> clazz = Class.forName(info.getName(), false, PluginCore.class.getClassLoader());
                    injector.inject(clazz);
                    Config config = clazz.getAnnotation(Config.class);
                    String id = config.id();
                    if (CONFIG_ID.matcher(id).matches()) {
                        if (config.separate()) {
                            if (id.equalsIgnoreCase(plugin.id())) {
                                plugin.console("External config [" + id + "] can't be same with plugin id.");
                            } else if (externalConfigs.containsKey(id)) {
                                plugin.console("External config [" + id + "] exist.");
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
                                plugin.console("Scan and inject external config [" + id + "] .");
                            }
                        } else {
                            if (internalConfigs.containsKey(id)) {
                                plugin.console("Internal config [" + id + "] exist.");
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
                                plugin.console("Scan and inject internal config [" + id + "] .");
                            }
                        }
                    } else {
                        plugin.console("Illegal config id [" + id + "], please use [a-zA-Z_].");
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private @NotNull Consumer<ClassInfo> commandScanner() {
        return info -> {
            if (info.hasAnnotation(Cmd.class)) {
                plugin.consoleKey("tryInjectCommand", info.getName());
                try {
                    Class<?> clazz = Class.forName(info.getName(), false, PluginCore.class.getClassLoader());
                    injector.inject(clazz);
                    Cmd cmd = clazz.getAnnotation(Cmd.class);
                    try {
                        Object instance = clazz.newInstance();
                        injector.inject(instance);
                        CommandCore core = new CommandCore(plugin, cmd);
                        core.extractSub(instance);
                        core.extractTab(instance);
                        if (core.getName().equalsIgnoreCase(plugin.id())) {
                            if (clazz != BaseCommands.class) {
                                BaseCommands base = new BaseCommands();
                                injector.inject(base);
                                core.extractSub(base);
                                core.extractTab(base);
                            }
                            mainCommand = core;
                        } else {
                            commands.add(core);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private @NotNull Consumer<ClassInfo> listenerScanner() {
        return info -> {
            if (info.hasAnnotation(InjectListener.class)) {
                plugin.consoleKey("tryInjectListener", info.getName());
                try {
                    Class<?> clazz = Class.forName(info.getName(), false, PluginCore.class.getClassLoader());
                    injector.inject(clazz);
                    try {
                        Object instance = clazz.newInstance();
                        injector.inject(instance);
                        listeners.add(instance);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private @NotNull Consumer<ClassInfo> injectScanner() {
        return info -> {
            if (info.hasAnnotation(Inject.class) || info.hasAnnotation(Config.class) || info.hasAnnotation(Cmd.class) || info.hasAnnotation(InjectListener.class)) {
                try {
                    Class<?> clazz = Class.forName(info.getName(), false, PluginCore.class.getClassLoader());
                    injector.inject(clazz);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        };
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
        plugin.onPluginLoad();
    }

    public void onEnable() {
        Set<ClassInfo> classes = PluginScanner.scan(plugin.getJarFile(), info -> info.matchMcVersion(MC_VERSION));
        classes.forEach(info -> configScanner().accept(info));
        load();
        classes.forEach(info -> commandScanner().accept(info));
        classes.forEach(info -> listenerScanner().accept(info));
        classes.forEach(info -> injectScanner().accept(info));
        if (mainCommand == null) {
            mainCommand = new CommandCore(plugin);
            BaseCommands base = new BaseCommands();
            injector.inject(base);
            mainCommand.extractSub(base);
            mainCommand.extractTab(base);
        }
        if (!plugin.registerCommand(mainCommand)) {
            plugin.consoleKey("mainCmdRegFail", mainCommand.getName());
        }
        commands.forEach(command -> {
            if (!plugin.registerCommand(command)) {
                plugin.consoleKey("commandRegFailed", command.getName());
            }
        });
        listeners.forEach(plugin::registerListener);
        plugin.onPluginEnable();
        enableActions.forEach(Runnable::run);
        plugin.consoleKey("pluginEnabled", plugin.id() + "-" + plugin.version());
        if (plugin.id().equalsIgnoreCase(Violet.PLUGIN_ID)) {
            plugin.consoleLogKey("serverRunning", getServerId(), MC_VERSION);
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

    public static @Nullable IPlugin getPlugin(@NotNull String id) {
        return PLUGINS.get(id);
    }

    public static @NotNull List<IPlugin> getPlugins() {
        return new ArrayList<>(PLUGINS.values());
    }
}
