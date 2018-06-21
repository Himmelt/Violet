package org.soraworld.violet.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.bukkit.Bukkit;
import org.soraworld.violet.api.OperationManager;
import org.soraworld.violet.constant.Violets;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import rikka.RikkaAPI;
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
            setLang(setting.lang);
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
            if (Files.notExists(file)) {
                Files.createDirectories(file.getParent());
                Files.copy(this.getClass().getResourceAsStream("/lang/" + lang + ".lang"), file);
            }
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

    public String trans(String key, Object... args) {
        String text = langNode.getNode(key).getString();
        return text == null || text.isEmpty() ? key : String.format(text, args);
    }

    public static String colorize(String text) {
        return text == null ? null : text.replace('&', Violets.COLOR_CHAR);
    }

    public void sendMsg(ICommandSender sender, String msg) {
        sender.sendMsg(msg);
    }

    public void sendKey(ICommandSender sender, String key, Object... args) {
        sender.sendMsg(colorize(trans(key, args)));
    }

    public void broadcast(String msg) {
        if (RikkaAPI.BUKKIT) {
            Bukkit.broadcastMessage(msg);
        } else if (RikkaAPI.SPONGE) {
            Sponge.getServer().getBroadcastChannel().send(Text.of(msg));
        }
    }

    public void broadcastKey(String key, Object... args) {
        broadcast(trans(key, args));
    }

    public void console(String msg) {
        if (RikkaAPI.BUKKIT) {
            Bukkit.getConsoleSender().sendMessage(colorize(msg));
        } else if (RikkaAPI.SPONGE) {
            Sponge.getServer().getConsole().sendMessage(Text.of(colorize(msg)));
        }
    }

    public void consoleKey(String key, Object... args) {
        console(trans(key, args));
    }

    public void println(String msg) {
        // TODO plainHead
        System.out.println("plainHead " + msg);
    }

    public VioletSetting getSetting() {
        return setting;
    }

    public String adminPerm() {
        return Violets.PERM_ADMIN;
    }

}
