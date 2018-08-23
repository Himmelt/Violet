package org.soraworld.violet.manager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.plugin.SpigotPlugin;
import org.soraworld.violet.util.ChatColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Spigot 管理器.
 */
public abstract class SpigotManager extends VioletManager<SpigotPlugin> {

    /**
     * 实例化管理器.
     *
     * @param plugin 插件实例
     * @param path   配置保存路径
     */
    public SpigotManager(SpigotPlugin plugin, Path path) {
        super(plugin, path);
    }

    public void asyncSave() {
        if (!asyncLock) {
            asyncLock = true;
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                save();
                asyncLock = false;
            });
        }
    }

    public String trans(@Nonnull String key, Object... args) {
        String text = langMap.get(key);
        // fallback to Violet
        if (text == null || text.isEmpty()) text = Manager.trans(lang, key);
        return (text == null || text.isEmpty()) ? key : args.length > 0 ? String.format(text, args) : text;
    }

    /**
     * 发送消息.
     * 颜色请使用 {@link ChatColor}
     *
     * @param sender  消息接收者
     * @param message 消息内容
     */
    public void send(@Nonnull CommandSender sender, @Nonnull String message) {
        sender.sendMessage(colorHead + message);
    }

    /**
     * 发送消息翻译.
     *
     * @param sender 消息接收者
     * @param key    键
     * @param args   参数
     */
    public void sendKey(@Nonnull CommandSender sender, @Nonnull String key, Object... args) {
        send(sender, trans(key, args));
    }

    public void console(@Nonnull String text) {
        Bukkit.getConsoleSender().sendMessage(colorHead + text);
    }

    public void broadcast(@Nonnull String text) {
        Bukkit.broadcastMessage(colorHead + text);
    }

    /**
     * Violet 管理器.
     */
    public static class Manager extends SpigotManager {

        private static Manager manager;
        private static HashMap<String, HashMap<String, String>> langMaps = new HashMap<>();

        /**
         * 实例化管理器.
         *
         * @param plugin 插件实例
         * @param path   配置保存路径
         */
        public Manager(SpigotPlugin plugin, Path path) {
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

        /**
         * 全局翻译.
         *
         * @param lang 翻译语言
         * @param key  键
         * @param args 参数
         * @return 全局翻译结果
         */
        public static String trans(String lang, String key, Object... args) {
            String text = langMaps.computeIfAbsent(lang, s -> manager.loadLangMap(s)).get(key);
            if (text == null || text.isEmpty()) {
                if (manager != null) return manager.trans(key, args);
                else return key;
            } else return text;
        }

        /**
         * 向命令发送者显示 violet 插件列表.
         *
         * @param sender 命令发送者
         */
        public void listPlugins(CommandSender sender) {
            for (IPlugin plugin : plugins) {
                sendKey(sender, "pluginInfo", plugin.getId(), plugin.getVersion());
            }
        }
    }
}
