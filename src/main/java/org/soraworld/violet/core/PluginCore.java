package org.soraworld.violet.core;

import org.jetbrains.annotations.NotNull;
import org.soraworld.hocon.exception.SerializerException;
import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Options;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.api.I18n;
import org.soraworld.violet.api.*;
import org.soraworld.violet.asm.ClassInfo;
import org.soraworld.violet.asm.PluginScanner;
import org.soraworld.violet.command.CommandCore;
import org.soraworld.violet.inject.Command;
import org.soraworld.violet.inject.Injector;
import org.soraworld.violet.inject.Manager;
import org.soraworld.violet.manager.Translator;
import org.soraworld.violet.serializers.UUIDSerializer;
import org.soraworld.violet.text.ChatColor;
import org.soraworld.violet.text.JsonText;
import org.soraworld.violet.util.FileUtils;

import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class PluginCore extends IManager, IMessenger, I18n {

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
    private final HashMap<String, String> langMap = new HashMap<>();
    private final HashMap<String, IConfig> configs = new HashMap<>();
    private final TreeMap<Integer, List<Consumer<ClassInfo>>> injectorMap = new TreeMap<>();
    private final AtomicBoolean asyncSaveLock = new AtomicBoolean(false);
    private final AtomicBoolean asyncBackLock = new AtomicBoolean(false);

    /* ---------------------------- Statics ---------------------------- */
    private static final ArrayList<IPlugin> PLUGINS = new ArrayList<>();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
    static Translator translator = null;

    public PluginCore(@NotNull IPlugin plugin) {
        this.plugin = plugin;
        this.injector.addValue(plugin);
        this.rootPath = plugin.getRootPath();
        this.classes.addAll(PluginScanner.scan(plugin.getJarFile()));
        this.options.setTranslator(Options.COMMENT, this::trans);
        this.options.setTranslator(Options.READ, ChatColor::colorize);
        this.options.setTranslator(Options.WRITE, ChatColor::fakerize);
        try {
            this.options.registerType(new UUIDSerializer());
        } catch (SerializerException e) {
            console(ChatColor.RED + "TypeSerializer for UUID register failed");
            e.printStackTrace();
        }
        this.confile = rootPath.resolve(plugin.getId().replace(' ', '_') + ".conf");
        this.rootNode = new FileNode(confile.toFile(), options);
        setHead(defChatHead());
        if (!PLUGINS.contains(plugin)) {
            PLUGINS.add(plugin);
        }
    }

    public static void listPlugins() {

    }

    public void setHead(String head) {
        this.colorHead = defChatColor() + ChatColor.colorize(head) + ChatColor.RESET;
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
                console(ChatColor.RED + "Lang file " + langFile + " load exception !!!");
            } else {
                console(ChatColor.RED + "Lang file " + url + " extract exception !!!");
            }
            debug(e);
            return new HashMap<>();
        }
    }

    @Override
    public boolean load() {
        if (Files.notExists(confile)) {
            setLang(DEF_LANG);
            save();
            return true;
        }
        try {
            rootNode.load(true, true);
            rootNode.modify(this);
            if (!setLang(lang) && !DEF_LANG.equalsIgnoreCase(lang)) {
                setLang(DEF_LANG);
            }
            setDebug(debug);
            reloadSuccess = true;
            if (!plugin.getVersion().equalsIgnoreCase(version)) {
                consoleKey("versionChanged", version, plugin.getVersion());
                if (autoBackUp) {
                    consoleKey(doBackUp() ? "backUpSuccess" : "backUpFailed");
                }
                if (autoUpLang) {
                    consoleKey(extract() ? "reExtracted" : "reExtractFailed");
                }
            }
            return true;
        } catch (Throwable e) {
            console(ChatColor.RED + "Config file load exception !!!");
            e.printStackTrace();
            reloadSuccess = false;
            return false;
        }
    }

    @Override
    public boolean save() {
        reloadSuccess = true;
        version = plugin.getVersion();
        try {
            rootNode.extract(this);
            rootNode.save();
            return true;
        } catch (Throwable e) {
            console(ChatColor.RED + "Config file save exception !!!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean doBackUp() {
        Path target = rootPath.resolve("backup/" + DATE_FORMAT.format(new Date()) + ".zip");
        return FileUtils.zipArchivePath(rootPath, target, p -> {
            String name = rootPath.relativize(p).toString().toLowerCase();
            return !name.startsWith("backup");
        });
    }

    // TODO 队列
    public void asyncBackUp(Consumer<Boolean> callback) {
        if (!asyncBackLock.get()) {
            asyncBackLock.set(true);
            plugin.runTaskAsync(() -> {
                boolean flag = doBackUp();
                plugin.runTask(() -> callback.accept(flag));
                asyncBackLock.set(false);
            });
        } else {
            consoleKey("asyncBackupBlock");
        }
    }

    public void asyncBackUp(@NotNull ICommandSender sender, BiConsumer<Boolean, ICommandSender> callback) {
        if (!asyncBackLock.get()) {
            asyncBackLock.set(true);
            plugin.runTaskAsync(() -> {
                boolean flag = doBackUp();
                plugin.runTask(() -> callback.accept(flag, sender));
                asyncBackLock.set(false);
            });
        }
    }

    public boolean reExtract() {
        if (FileUtils.deletePath(rootPath.resolve("lang").toFile(), debug)) {
            return setLang(lang);
        }
        if (debug) {
            console(ChatColor.RED + "deletePath " + rootPath.resolve("lang") + " failed !!");
        }
        return false;
    }

    @Override
    public String getLang() {
        return lang;
    }

    @Override
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

    @Override
    public boolean isDebug() {
        return debug;
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
        options.setDebug(debug);
    }

    @Override
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

    public void broadcastKey(String key, Object... args) {
        broadcast(trans(key, args));
    }

    @Override
    public void consoleKey(String key, Object... args) {
        console(trans(key, args));
    }

    @Override
    public void log(@NotNull String text) {

    }

    @Override
    public void logKey(@NotNull String key, Object... args) {
        log(trans(key, args));
    }

    @Override
    public void consoleLog(@NotNull String text) {
        console(text);
        log(text);
    }

    @Override
    public void consoleLogKey(@NotNull String key, Object... args) {
        String text = trans(key, args);
        console(text);
        log(text);
    }

    public void println(@NotNull String text) {
        System.out.println(plainHead + text);
    }

    public Path getRootPath() {
        return rootPath;
    }

    public boolean canSaveOnDisable() {
        return saveOnDisable && reloadSuccess;
    }

    public String defChatHead() {
        return "[" + getPlugin().getName() + "] ";
    }

    public String defAdminPerm() {
        return getPlugin().getId() + ".admin";
    }

    @Override
    public void debugKey(String key, Object... args) {
        if (isDebug()) {
            consoleKey(key, args);
        }
    }

    @Override
    public void debug(String text) {
        if (isDebug()) {
            console(text);
        }
    }

    @Override
    public void debug(Throwable e) {
        if (isDebug()) {
            e.printStackTrace();
        }
    }

    public void beforeLoad() {
    }

    public void afterLoad() {
    }

    public abstract ChatColor defChatColor();

    @Override
    public abstract void console(String text);

    @Override
    public abstract void broadcast(String message);

    public void addInjector(int priority, @NotNull Consumer<ClassInfo> injector) {
        priority = Math.max(0, Math.min(10, priority));
        injectorMap.computeIfAbsent(priority, i -> new ArrayList<>()).add(injector);
    }

    public void inject() {
        for (int i = 0; i <= 10; i++) {
            List<Consumer<ClassInfo>> injectors = injectorMap.get(i);
            if (injectors != null) {
                classes.forEach(clazz -> injectors.forEach(injector -> injector.accept(clazz)));
            }
        }
    }

    private void addInjectors(ClassLoader loader, Path rootPath) {
        addInjector(0, info -> {
            if (info.hasAnnotation(Manager.class)) {
                manager.console("Try construct @Manager -> " + info.getName());
                try {
                    Class<?> clazz = Class.forName(info.getName(), false, loader);
                    if (clazz != null && IConfig.class.isAssignableFrom(clazz)) {
                        for (Constructor<?> constructor : clazz.getConstructors()) {
                            Class<?>[] params = constructor.getParameterTypes();
                            if (params.length == 2 && IPlugin.class.isAssignableFrom(params[0]) && Path.class.equals(params[1])) {
                                constructor.setAccessible(true);
                                try {
                                    manager.setConfig((IConfig) constructor.newInstance(this, rootPath));
                                    break;
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        addInjector(1, info -> {
            if (info.hasAnnotation(Command.class)) {
                manager.console("Try construct @Command -> " + info.getName());
                try {
                    Class<?> clazz = Class.forName(info.getName(), false, loader);
                    try {
                        Object instance = clazz.getConstructor().newInstance();
                        injector.inject(instance);
                        CommandCore command = registerCommand(annotation);
                        if (command != null) {
                            command.extractSub(instance);
                            command.extractTab(instance);
                            if (clazz != BaseSubCmds.class && command.getName().equalsIgnoreCase(getId())) {
                                BaseSubCmds baseSubCmds = new BaseSubCmds();
                                injectIntoInstance(baseSubCmds);
                                command.extractSub(baseSubCmds);
                                command.extractTab(baseSubCmds);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        addInjector(1, info -> {

        });
    }

    public void onLoad() {
        Path rootPath = plugin.getRootPath();
        ClassLoader loader = plugin.getClassLoader();
        if (Files.notExists(rootPath)) {
            try {
                Files.createDirectories(rootPath);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        addInjectors(loader, rootPath);
        plugin.onPluginLoad();
    }

    public void onEnable() {
        inject();
        manager.load();
        plugin.onPluginEnable();
        manager.consoleKey("pluginEnabled", plugin.getId() + "-" + plugin.getVersion());
    }

    public void onDisable() {
        plugin.onPluginDisable();
        manager.consoleKey("pluginDisabled", plugin.getId() + "-" + plugin.getVersion());
        if (manager.isSaveOnDisable()) {
            manager.consoleKey(manager.save() ? "configSaved" : "configSaveFailed");
        }
    }
}
