package org.soraworld.violet.manager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.core.ManagerCore;
import org.soraworld.violet.nms.Version;
import org.soraworld.violet.plugin.SpigotPlugin;
import org.soraworld.violet.text.JsonText;
import org.soraworld.violet.util.ChatColor;

import java.nio.file.Path;

/**
 * @author Himmelt
 */
public final class SpigotManager implements IManager {

    private final ManagerCore core;

    public SpigotManager(SpigotPlugin plugin, Path path) {
        core = new ManagerCore(plugin, path);
    }

    public void asyncSave(@Nullable CommandSender sender) {
        if (!asyncSaveLock.get()) {
            asyncSaveLock.set(true);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                boolean flag = save();
                if (sender != null) {
                    Bukkit.getScheduler().runTask(plugin, () -> sendKey(sender, flag ? "configSaved" : "configSaveFailed"));
                }
                asyncSaveLock.set(false);
            });
        }
    }

    public void asyncBackUp(@Nullable CommandSender sender) {
        if (!asyncBackLock.get()) {
            asyncBackLock.set(true);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                boolean flag = doBackUp();
                if (sender != null) {
                    Bukkit.getScheduler().runTask(plugin, () -> sendKey(sender, flag ? "backUpSuccess" : "backUpFailed"));
                }
                asyncBackLock.set(false);
            });
        }
    }

    public void send(CommandSender sender, String message) {
        sender.sendMessage(colorHead + message);
    }

    public void sendKey(CommandSender sender, String key, Object... args) {
        send(sender, trans(key, args));
    }

    public void sendJson(Player player, JsonText... texts) {
        String commandLine = "tellraw " + player.getName() + " " + ChatColor.colorize(JsonText.toJson(jsonHead, texts));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine);
        debug(commandLine);
    }

    public void sendActionBar(Player player, String text) {
        if (Version.v1_7_R4) {
            send(player, text);
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
        }
    }

    public void sendActionKey(Player player, String key, Object... args) {
        if (Version.v1_7_R4) {
            sendKey(player, key, args);
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(trans(key, args)));
        }
    }

    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (Version.v1_7_R4) {
            send(player, title);
        } else {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    @Override
    public ChatColor defChatColor() {
        return ChatColor.WHITE;
    }

    @Override
    public void console(String text) {
        Bukkit.getConsoleSender().sendMessage(colorHead + text);
    }

    @Override
    public void broadcast(String text) {
        Bukkit.broadcastMessage(colorHead + text);
    }

    public boolean hasPermission(Permissible subject, String permission) {
        if (permission == null || permission.isEmpty()) {
            return true;
        }
        permission = permMap.getOrDefault(permission, permission);
        return permission == null || permission.isEmpty() || subject.hasPermission(permission);
    }

    @Override
    public boolean load() {
        return core.load();
    }

    @Override
    public void consoleKey(String key, String s) {

    }

    public ManagerCore getCore() {
        return core;
    }
}
