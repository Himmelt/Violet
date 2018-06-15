package org.soraworld.violet.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.soraworld.violet.api.OperationManager;
import org.soraworld.violet.constant.Violets;
import rikka.api.command.ICommandSender;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static java.util.Locale.CHINA;

public class VioletManager implements OperationManager {

    private final Path path;
    private final Path confile;
    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private final ConfigurationOptions options = ConfigurationOptions.defaults().setShouldCopyDefaults(true);
    protected VioletSetting setting;
    protected ConfigurationNode langNode = SimpleConfigurationNode.root();
    protected CommentedConfigurationNode rootNode = SimpleCommentedConfigurationNode.root();

    private static final TypeToken<VioletSetting> TOKEN = TypeToken.of(VioletSetting.class);

    public VioletManager(Path path, VioletSetting setting) {
        this.path = path;
        this.confile = path.resolve("config.conf");
        this.loader = HoconConfigurationLoader.builder().setPath(confile).build();
        this.setting = setting;
    }

    public boolean load() {
        if (Files.notExists(confile)) {
            setLang(CHINA.equals(Locale.getDefault()) ? "zh_cn" : "en_us");
            save();
            return true;
        }
        try {
            rootNode = loader.load(options);
            setting = rootNode.getValue(TOKEN, setting);
            return true;
        } catch (Throwable e) {
            console("&cConfig file load exception !!!");
            if (setting.debug) e.printStackTrace();
            return false;
        }
    }

    public boolean save() {
        try {
            rootNode.setValue(TOKEN, setting);
            loader.save(rootNode);
            return true;
        } catch (Throwable e) {
            console("&cConfig file save exception !!!");
            if (setting.debug) e.printStackTrace();
            return false;
        }
    }

    public void setLang(String lang) {
        setting.lang = lang;
        Path file = path.resolve("lang").resolve(lang + ".lang");
        ConfigurationLoader loader = HoconConfigurationLoader.builder().setPath(file).build();
        boolean extract = false;
        try {
            if (Files.notExists(file)) Files.copy(this.getClass().getResourceAsStream(""), file);
            extract = true;
            langNode = loader.load();
        } catch (Throwable e) {
            if (extract) console("&cLang file " + lang + " load exception !!!");
            else console("&cLang file " + lang + " extract exception !!!");
            if (setting.debug) e.printStackTrace();
            langNode = SimpleConfigurationNode.root();
        }
    }

    public String trans(String key) {
        // TODO xx.b.c ?
        return langNode.getNode(key).getString();
    }

    public void sendMsg(ICommandSender sender, String msg) {
        sender.sendMsg(msg);
    }

    public void sendKey(ICommandSender sender, String key, Object... args) {
        sender.sendMsg(key);
    }

    public void broadcast(String msg) {

    }

    public void broadcastKey(String key, Object... args) {

    }

    public void console(String msg) {
    }

    public void consoleKey(String key, Object... args) {
        System.out.println(String.format(key, args));
    }

    public void println(String msg) {
        System.out.println("plainHead " + msg);
    }

    public VioletSetting getSetting() {
        return setting;
    }

    public String adminPerm() {
        return Violets.PERM_ADMIN;
    }

}
