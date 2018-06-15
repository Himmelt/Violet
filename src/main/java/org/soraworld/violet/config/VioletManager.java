package org.soraworld.violet.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.soraworld.violet.api.OperationManager;
import org.soraworld.violet.constant.Violets;
import rikka.api.command.ICommandSender;

import java.nio.file.Path;

public class VioletManager implements OperationManager {

    private final Path path;
    private final Path confile;
    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private final ConfigurationOptions options = ConfigurationOptions.defaults().setShouldCopyDefaults(true);
    protected VioletSetting setting;
    protected CommentedConfigurationNode rootNode;

    private TypeToken<VioletSetting> TOKEN = TypeToken.of(VioletSetting.class);

    public VioletManager(Path path, VioletSetting setting) {
        this.path = path;
        this.confile = path.resolve("config.conf");
        this.loader = HoconConfigurationLoader.builder().setPath(confile).build();
        this.setting = setting;
    }

    public boolean load() {
        try {
            rootNode = loader.load(options);
            setting = rootNode.getValue(TOKEN, setting);
            return true;
        } catch (Throwable e) {
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
            if (setting.debug) e.printStackTrace();
            return false;
        }
    }

    public void sendMsg(ICommandSender sender, String msg) {
        sender.sendMsg(msg);
    }

    public void sendKey(ICommandSender sender, String key, Object... args) {
        sender.sendMsg(key);
    }

    public void sendVKey(ICommandSender sender, String key, Object... args) {
        sender.sendMsg(key);
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

    public void vSendKey(ICommandSender sender, String key, Object... args) {

    }

    public void vBroadcastKey(String key, Object... args) {
    }

    public VioletSetting getSetting() {
        return setting;
    }

    public String adminPerm() {
        return Violets.PERM_ADMIN;
    }

}
