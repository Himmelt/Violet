package org.soraworld.violet.manager;

import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.util.ChatColor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashMap;

public abstract class SpongeManager extends VioletManager {

    public SpongeManager(IPlugin plugin, Path path) {
        super(plugin, path);
    }

    public String trans(@Nonnull String key, Object... args) {
        String text = langMap.get(key);
        // fallback to Violet
        if (text == null || text.isEmpty()) text = Manager.trans(lang, key);
        return (text == null || text.isEmpty()) ? key : args.length > 0 ? String.format(text, args) : text;
    }

    public void send(@Nonnull CommandSource sender, @Nonnull String message) {
        sender.sendMessage(Text.of(colorHead + message));
    }

    public void sendKey(@Nonnull CommandSource sender, @Nonnull String key, Object... args) {
        send(sender, trans(key, args));
    }

    public void console(@Nonnull String text) {
        Sponge.getServer().getConsole().sendMessage(Text.of(colorHead + text));
    }

    public void broadcast(@Nonnull String text) {
        Sponge.getServer().getBroadcastChannel().send(Text.of(colorHead + text));
    }

    public static class Manager extends SpongeManager {

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
    }
}
