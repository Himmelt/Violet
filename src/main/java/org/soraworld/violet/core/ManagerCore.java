package org.soraworld.violet.core;

import org.soraworld.hocon.exception.SerializerException;
import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Options;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.api.IConfig;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.serializers.UUIDSerializer;
import org.soraworld.violet.util.ChatColor;
import org.soraworld.violet.util.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Himmelt
 */
public final class ManagerCore {

    @Setting
    private String version = "0.0.0";
    @Setting
    private boolean autoBackUp = true;
    @Setting
    private boolean saveOnDisable = true;

    private final Path root;
    private final Path confile;
    private final IPlugin plugin;
    private final IManager manager;
    private final FileNode rootNode;
    private boolean reloadSuccess = false;

    private final I18n i18n = new I18n();
    private final MessengerCore messenger = new MessengerCore();
    private final Options options = Options.build();
    private final AtomicBoolean asyncSaveLock = new AtomicBoolean(false);
    private final AtomicBoolean asyncBackLock = new AtomicBoolean(false);
    private final HashMap<String, IConfig> configs = new HashMap<>();

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");

    public ManagerCore(IPlugin plugin, IManager manager, Path root) {
        this.root = root;
        this.plugin = plugin;
        this.manager = manager;
        this.options.setTranslator(Options.COMMENT, i18n::trans);
        this.options.setTranslator(Options.READ, ChatColor::colorize);
        this.options.setTranslator(Options.WRITE, ChatColor::fakerize);
        this.options.registerType(new UUIDSerializer());

        this.confile = root.resolve(plugin.getId().replace(' ', '_') + ".conf");
        this.rootNode = new FileNode(confile.toFile(), options);
        this.messenger.setHead(plugin.getName());
    }

    public static void listPlugins() {

    }

    public void beforeLoad() {
        try {
        } catch (SerializerException e) {
            console(ChatColor.RED + "TypeSerializer for UUID register failed");
            e.printStackTrace();
        }
    }

    public boolean load() {

        if (config != null) {
            config.beforeLoad();
        }

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
            setDebug(debug);
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
        } catch (Throwable e) {
            console(ChatColor.RED + "Config file load exception !!!");
            e.printStackTrace();
            reloadSuccess = false;
            return false;
        }

        if (config != null) {
            config.afterLoad();
        }
        return true;
    }

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
            e.printStackTrace();
            return false;
        }
    }

    public boolean doBackUp() {
        Path target = root.resolve("backup/" + DATE_FORMAT.format(new Date()) + ".zip");
        return FileUtils.zipArchivePath(root, target, p -> {
            String name = root.relativize(p).toString().toLowerCase();
            return !name.startsWith("backup");
        });
    }


    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
        options.setDebug(debug);
    }

    public IPlugin getPlugin() {
        return plugin;
    }

    public Path getRoot() {
        return root;
    }

    public boolean isSaveOnDisable() {
        return saveOnDisable && reloadSuccess;
    }

    public String defChatHead() {
        return "[" + getPlugin().getName() + "] ";
    }

    public String defAdminPerm() {
        return getPlugin().getId() + ".admin";
    }

    public void beforeLoad() {
    }

    public void afterLoad() {
    }

    public String mappingPerm(String perm) {
        return permMap.getOrDefault(perm, perm);
    }

    public void setConfig(IConfig config) {
        this.config = config;
    }

    public IConfig getConfig() {
        return config;
    }
}
