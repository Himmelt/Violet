package org.soraworld.violet.manager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.util.ChatColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashMap;

public abstract class SpigotManager extends VioletManager {

    public SpigotManager(IPlugin plugin, Path path) {
        super(plugin, path);
    }

    public String trans(@Nonnull String key, Object... args) {
        String text = langMap.get(key);
        // fallback to Violet
        if (text == null || text.isEmpty()) text = Manager.trans(lang, key);
        return (text == null || text.isEmpty()) ? key : args.length > 0 ? String.format(text, args) : text;
    }

    public void send(@Nonnull CommandSender sender, @Nonnull String message) {
        sender.sendMessage(colorHead + message.replace('&', ChatColor.COLOR_CHAR));
    }

    public void sendKey(@Nonnull CommandSender sender, @Nonnull String key, Object... args) {
        send(sender, trans(key, args));
    }

    public void console(@Nonnull String text) {
        Bukkit.getConsoleSender().sendMessage(colorHead + text.replace('&', ChatColor.COLOR_CHAR));
    }

    public void broadcast(@Nonnull String text) {
        Bukkit.broadcastMessage(colorHead + text.replace('&', ChatColor.COLOR_CHAR));
    }

    public static class Manager extends SpigotManager {

        private static Manager manager;
        private static HashMap<String, HashMap<String, String>> langMaps = new HashMap<>();

        public Manager(IPlugin plugin, Path path) {
            super(plugin, path);
            manager = this;
        }

        public String trans(@Nonnull String key, Object... args) {
            String text = langMap.get(key);
            return (text == null || text.isEmpty()) ? key : args.length > 0 ? String.format(text, args) : text;
        }

        @Nonnull
        public String defChatHead() {
            return "[" + Violet.PLUGIN_NAME + "] ";
        }

        @Nonnull
        public ChatColor defChatColor() {
            return ChatColor.DARK_PURPLE;
        }

        public void beforeLoad() {
        }

        @Nullable
        public String defAdminPerm() {
            return Violet.PERM_ADMIN;
        }

        public void afterLoad() {
        }

        public static String trans(String lang, String key, Object... args) {
            String text = langMaps.computeIfAbsent(lang, s -> manager.loadLangMap(s)).get(key);
            if (text == null || text.isEmpty()) {
                if (manager != null) return manager.trans(key, args);
                else return key;
            } else return text;
        }

        public void listPlugins(CommandSender sender) {
            for (IPlugin plugin : plugins) {
                sendKey(sender, "pluginInfo", plugin.getId(), plugin.getVersion());
            }
        }
    }
}
