package org.soraworld.violet.chat;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IIChat {

    private String PLAIN_HEAD;
    private String COLOR_HEAD;
    private IChatBaseComponent CHAT_HEAD;
    private final ChatColor prefix;

    private static final Pattern FORMAT = Pattern.compile("((?<!&)&[0-9a-fk-or])+");

    public IIChat(@Nonnull String chatHead, @Nonnull ChatColor prefix) {
        PLAIN_HEAD = chatHead;//"[" + Constant.PLUGIN_NAME + "] ";
        COLOR_HEAD = prefix + PLAIN_HEAD + ChatColor.RESET;
        ChatModifier style = new ChatModifier();
        style.setColor(EnumChatFormat.b(prefix.name()));
        CHAT_HEAD = new ChatComponentText(chatHead).setChatModifier(style);
        this.prefix = prefix;
    }

    public void setHead(String rawHead) {
        if (rawHead != null && !rawHead.isEmpty()) {
            PLAIN_HEAD = rawHead;
            COLOR_HEAD = prefix + PLAIN_HEAD.replace('&', ChatColor.COLOR_CHAR) + ChatColor.RESET;
            CHAT_HEAD = format(prefix + rawHead);
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

    private static ChatModifier parseStyle(String text) {
        ChatModifier style = new ChatModifier();
        int length = text.length();
        for (int i = 1; i < length; i += 2) {
            switch (text.charAt(i)) {
                case '0':
                    style.setColor(EnumChatFormat.BLACK);
                    break;
                case '1':
                    style.setColor(EnumChatFormat.DARK_BLUE);
                    break;
                case '2':
                    style.setColor(EnumChatFormat.DARK_GREEN);
                    break;
                case '3':
                    style.setColor(EnumChatFormat.DARK_AQUA);
                    break;
                case '4':
                    style.setColor(EnumChatFormat.DARK_RED);
                    break;
                case '5':
                    style.setColor(EnumChatFormat.DARK_PURPLE);
                    break;
                case '6':
                    style.setColor(EnumChatFormat.GOLD);
                    break;
                case '7':
                    style.setColor(EnumChatFormat.GRAY);
                    break;
                case '8':
                    style.setColor(EnumChatFormat.DARK_GRAY);
                    break;
                case '9':
                    style.setColor(EnumChatFormat.BLUE);
                    break;
                case 'a':
                    style.setColor(EnumChatFormat.GREEN);
                    break;
                case 'b':
                    style.setColor(EnumChatFormat.AQUA);
                    break;
                case 'c':
                    style.setColor(EnumChatFormat.RED);
                    break;
                case 'd':
                    style.setColor(EnumChatFormat.LIGHT_PURPLE);
                    break;
                case 'e':
                    style.setColor(EnumChatFormat.YELLOW);
                    break;
                case 'f':
                    style.setColor(EnumChatFormat.WHITE);
                    break;
                case 'k':
                    style.setRandom(true);
                    break;
                case 'l':
                    style.setBold(true);
                    break;
                case 'm':
                    style.setStrikethrough(true);
                    break;
                case 'n':
                    style.setUnderline(true);
                    break;
                case 'o':
                    style.setItalic(true);
                    break;
                default:
                    style = new ChatModifier();
            }
        }
        return style;
    }

    public static IChatBaseComponent format(String text) {
        return format(text, null, null, null, null);
    }

    public static IChatBaseComponent format(String text, EnumClickAction ca, String cv, EnumHoverAction ha, String hv) {
        Matcher matcher = FORMAT.matcher(text);
        IChatBaseComponent component = new ChatComponentText("");
        int head = 0;
        ChatModifier style = new ChatModifier();
        while (matcher.find()) {
            component.addSibling(new ChatComponentText(text.substring(head, matcher.start()).replaceAll("&&", "&")).setChatModifier(style));
            style = parseStyle(matcher.group());
            head = matcher.end();
        }
        component.addSibling(new ChatComponentText(text.substring(head).replaceAll("&&", "&")).setChatModifier(style));
        if (ca != null && cv != null) {
            component.getChatModifier().setChatClickable(new ChatClickable(ca, cv));
        }
        if (ha != null && hv != null) {
            component.getChatModifier().a(new ChatHoverable(ha, format(hv)));
        }
        return component;
    }

    public void sendMessage(Player player, IChatBaseComponent... siblings) {
        if (player instanceof CraftPlayer) {
            EntityPlayer playerMP = ((CraftPlayer) player).getHandle();
            IChatBaseComponent chatMessage = CHAT_HEAD.f();
            for (IChatBaseComponent component : siblings) {
                chatMessage.addSibling(component);
            }
            playerMP.b(chatMessage);
        }
    }

}
