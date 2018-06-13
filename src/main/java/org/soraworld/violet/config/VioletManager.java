package org.soraworld.violet.config;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.soraworld.violet.api.OperationManager;
import org.spongepowered.api.text.Text;
import rikka.api.command.ICommandSender;
import rikka.api.entity.living.IPlayer;

import java.nio.file.Path;

public class VioletManager implements OperationManager {

    private final Path path;
    private final Path confile;
    private final ConfigurationLoader loader;
    private final ConfigurationOptions options = ConfigurationOptions.defaults().setShouldCopyDefaults(true);
    public boolean debug;
    public String lang;
    public String adminPerm;
    private VioletSetting setting;
    private CommentedConfigurationNode rootNode = null;

    public VioletManager(Path path, VioletSetting setting) {
        this.path = path;
        this.confile = path.resolve("config.conf");
        this.loader = HoconConfigurationLoader.builder().setPath(confile).build();
        this.setting = setting;
    }

    public boolean load() {
        return true;
    }

    public boolean save() {
        return true;
    }

    public void sendMsg(ICommandSender sender, String msg) {

    }

    public void sendKey(ICommandSender sender, String key, Object... args) {
        sender.sendMessage(Text.of("text"));
    }

    public void sendVKey(ICommandSender sender, String key, Object... args) {

    }

    public void sendMessage(IPlayer player, String msg) {

    }

    public void broadcastKey(String key, Object... args) {

    }

    public void console(String msg) {
    }

    public void consoleKey(String key, Object... args) {

    }

    public void println(String msg) {
        System.out.println("plainHead " + msg);
    }

    public void vSendKey(ICommandSender sender, String key, Object... args) {

    }

    public void vBroadcastKey(String key, Object... args) {
    }

}
