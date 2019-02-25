package org.soraworld.violet.manager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.soraworld.violet.nms.Version;
import org.soraworld.violet.plugin.SpigotPlugin;
import org.soraworld.violet.text.ClickText;
import org.soraworld.violet.text.HoverText;
import org.soraworld.violet.text.JsonText;
import org.soraworld.violet.util.ChatColor;

import java.nio.file.Path;

public abstract class VManager extends IManager<SpigotPlugin> {

    public VManager(SpigotPlugin plugin, Path path) {
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
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }

    public void sendActionKey(Player player, String key, Object... args) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(trans(key, args)));
    }

    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (Version.v1_7_R4) send(player, title);
        else player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public void checkUpdate(CommandSender sender) {
        if (checkUpdate) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                if (hasUpdate()) {
                    if (sender instanceof Player) {
                        sendJson((Player) sender, new JsonText(trans("hasUpdate")),
                                new JsonText(ChatColor.GREEN + plugin.updateURL(),
                                        new ClickText(plugin.updateURL(), ClickText.Action.OPEN_URL),
                                        new HoverText(trans("clickUpdate"), HoverText.Action.SHOW_TEXT)
                                )
                        );
                    } else {
                        sendKey(sender, "hasUpdate" + ChatColor.GREEN + plugin.updateURL());
                    }
                }
            });
        }
    }

    public void console(String text) {
        Bukkit.getConsoleSender().sendMessage(colorHead + text);
    }

    public void broadcast(String text) {
        Bukkit.broadcastMessage(colorHead + text);
    }
}
