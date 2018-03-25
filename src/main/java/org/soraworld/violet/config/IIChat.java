package org.soraworld.violet.config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public class IIChat {

    private String PLAIN_HEAD;
    private String COLOR_HEAD;
    private final ChatColor prefix;

    public IIChat(@Nonnull String chatHead, @Nonnull ChatColor prefix) {
        PLAIN_HEAD = chatHead;//"[" + Constant.PLUGIN_NAME + "] ";
        COLOR_HEAD = prefix + PLAIN_HEAD + ChatColor.RESET;
        this.prefix = prefix;
    }

    public void setHead(String head) {
        if (head != null && !head.isEmpty()) {
            PLAIN_HEAD = head;
            COLOR_HEAD = prefix + PLAIN_HEAD + ChatColor.RESET;
        }
    }

    public void send(CommandSender sender, String message) {
        sender.sendMessage(COLOR_HEAD + colorize(message));
    }

    public void broadcast(String message) {
        Bukkit.broadcastMessage(COLOR_HEAD + colorize(message));
    }

    public void console(String message) {
        Bukkit.getConsoleSender().sendMessage(COLOR_HEAD + colorize(message));
    }

    private String colorize(String message) {
        return message.replace('&', ChatColor.COLOR_CHAR);
    }

}
