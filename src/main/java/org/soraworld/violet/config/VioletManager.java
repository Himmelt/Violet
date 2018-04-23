package org.soraworld.violet.config;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.soraworld.rikka.command.CommandSource;
import org.soraworld.rikka.entity.living.player.Player;

import java.nio.file.Path;

public class VioletManager {

    private final Path path;
    private final Path confile;
    private final ConfigurationLoader loader;
    private final ConfigurationOptions options = ConfigurationOptions.defaults().setShouldCopyDefaults(true);
    public boolean debug;
    public String lang;
    public String adminPerm;
    private Settings settings;
    private CommentedConfigurationNode rootNode = null;

    public VioletManager(Path path, Settings settings) {
        this.path = path;
        this.confile = path.resolve("config.conf");
        this.loader = HoconConfigurationLoader.builder().setPath(confile).build();
        this.settings = settings;
    }

    public boolean load() {
        return true;
    }

    public boolean save() {
        return true;
    }

    public void sendKey(CommandSource sender, String key, Object... args) {
        sender.sendMessage("text");
    }

    public void sendMessage(Player player, String msg) {

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

    public void vSendKey(CommandSource sender, String key, Object... args) {

    }

    public void vBroadcastKey(String key, Object... args) {
    }

}
