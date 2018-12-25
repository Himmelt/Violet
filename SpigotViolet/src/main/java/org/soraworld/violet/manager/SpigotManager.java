package org.soraworld.violet.manager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.soraworld.violet.plugin.SpigotPlugin;
import org.soraworld.violet.text.JsonText;
import org.soraworld.violet.util.ChatColor;

import java.nio.file.Path;

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
        if (!asyncSaveLock) {
            asyncSaveLock = true;
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                save();
                asyncSaveLock = false;
            });
        }
    }

    public String trans(String key, Object... args) {
        String text = langMap.get(key);
        // fallback to Violet
        if (text == null || text.isEmpty()) text = FBManager.trans(lang, key);
        return (text == null || text.isEmpty()) ? key : args.length > 0 ? String.format(text, args) : text;
    }

    /**
     * 发送消息.
     * 颜色请使用 {@link ChatColor}
     *
     * @param sender  消息接收者
     * @param message 消息内容
     */
    public void send(CommandSender sender, String message) {
        sender.sendMessage(colorHead + message);
    }

    /**
     * 发送消息翻译.
     *
     * @param sender 消息接收者
     * @param key    键
     * @param args   参数
     */
    public void sendKey(CommandSender sender, String key, Object... args) {
        send(sender, trans(key, args));
    }

    public void sendJson(CommandSender sender, JsonText... texts) {
        String commandLine = "/tellraw " + sender.getName() + " " + ChatColor.colorize(JsonText.toJson(texts));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine);
        debug(commandLine);
    }

    public void console(String text) {
        Bukkit.getConsoleSender().sendMessage(colorHead + text);
    }

    public void broadcast(String text) {
        Bukkit.broadcastMessage(colorHead + text);
    }
}
