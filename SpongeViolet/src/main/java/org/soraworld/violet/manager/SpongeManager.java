package org.soraworld.violet.manager;

import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.plugin.SpongePlugin;
import org.soraworld.violet.util.ChatColor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Sponge 管理器.
 */
public abstract class SpongeManager extends VioletManager<SpongePlugin> {

    /**
     * 实例化管理器.
     *
     * @param plugin 插件实例
     * @param path   配置保存路径
     */
    public SpongeManager(SpongePlugin plugin, Path path) {
        super(plugin, path);
    }

    public void asyncSave() {
        if (!asyncLock) {
            asyncLock = true;
            Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> {
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
    public void send(@Nonnull CommandSource sender, @Nonnull String message) {
        sender.sendMessage(Text.of(colorHead + message));
    }

    /**
     * 发送消息翻译.
     *
     * @param sender 消息接收者
     * @param key    键
     * @param args   参数
     */
    public void sendKey(@Nonnull CommandSource sender, @Nonnull String key, Object... args) {
        send(sender, trans(key, args));
    }

    public void console(@Nonnull String text) {
        Sponge.getServer().getConsole().sendMessage(Text.of(colorHead + text));
    }

    public void broadcast(@Nonnull String text) {
        Sponge.getServer().getBroadcastChannel().send(Text.of(colorHead + text));
    }

    /**
     * Violet 管理器.
     */
    public static class Manager extends SpongeManager {

        private static Manager manager;
        private static HashMap<String, HashMap<String, String>> langMaps = new HashMap<>();

        /**
         * 实例化管理器.
         *
         * @param plugin 插件实例
         * @param path   配置保存路径
         */
        public Manager(SpongePlugin plugin, Path path) {
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
        public void listPlugins(CommandSource sender) {
            for (IPlugin plugin : plugins) {
                sendKey(sender, "pluginInfo", plugin.getId(), plugin.getVersion());
            }
        }
    }
}
