package org.soraworld.violet.manager;

import org.soraworld.violet.plugin.SpongePlugin;
import org.soraworld.violet.util.ChatColor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.nio.file.Path;

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
        if (!asyncSaveLock) {
            asyncSaveLock = true;
            Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> {
                save();
                asyncSaveLock = false;
            });
        }
    }

    public String trans(String key, Object... args) {
        String text = langMap.get(key);
        // fallback to Violet
        if (text == null || text.isEmpty()) text = FSManager.trans(lang, key);
        return (text == null || text.isEmpty()) ? key : args.length > 0 ? String.format(text, args) : text;
    }

    /**
     * 发送消息.
     * 颜色请使用 {@link ChatColor}
     *
     * @param sender  消息接收者
     * @param message 消息内容
     */
    public void send(CommandSource sender, String message) {
        sender.sendMessage(Text.of(colorHead + message));
    }

    /**
     * 发送消息翻译.
     *
     * @param sender 消息接收者
     * @param key    键
     * @param args   参数
     */
    public void sendKey(CommandSource sender, String key, Object... args) {
        send(sender, trans(key, args));
    }

    public void console(String text) {
        Sponge.getServer().getConsole().sendMessage(Text.of(colorHead + text));
    }

    public void broadcast(String text) {
        Sponge.getServer().getBroadcastChannel().send(Text.of(colorHead + text));
    }
}
