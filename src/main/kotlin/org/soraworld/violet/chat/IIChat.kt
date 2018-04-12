package org.soraworld.violet.chat

import net.minecraft.server.v1_7_R4.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.regex.Pattern

class IIChat(private var PLAIN_HEAD: String?, private val prefix: ChatColor) {
    private var COLOR_HEAD: String? = null
    private var CHAT_HEAD: IChatBaseComponent? = null

    init {
        COLOR_HEAD = prefix.toString() + PLAIN_HEAD + ChatColor.RESET
        val style = ChatModifier()
        style.setColor(EnumChatFormat.b(prefix.name))
        CHAT_HEAD = ChatComponentText(PLAIN_HEAD).setChatModifier(style)
    }

    fun setHead(rawHead: String?) {
        if (rawHead != null && !rawHead.isEmpty()) {
            PLAIN_HEAD = rawHead
            COLOR_HEAD = prefix.toString() + PLAIN_HEAD!!.replace('&', ChatColor.COLOR_CHAR) + ChatColor.RESET
            CHAT_HEAD = format(prefix.toString() + rawHead)
        }
    }

    fun send(sender: CommandSender, message: String) {
        sender.sendMessage(COLOR_HEAD!! + colorize(message))
    }

    fun broadcast(message: String) {
        Bukkit.broadcastMessage(COLOR_HEAD!! + colorize(message))
    }

    fun console(message: String) {
        Bukkit.getConsoleSender().sendMessage(COLOR_HEAD!! + colorize(message))
    }

    private fun colorize(message: String): String {
        return message.replace('&', ChatColor.COLOR_CHAR)
    }

    fun sendMessage(player: Player, vararg siblings: IChatBaseComponent) {
        if (player is CraftPlayer) {
            val playerMP = player.handle
            val chatMessage = CHAT_HEAD!!.f()
            for (component in siblings) {
                chatMessage.addSibling(component)
            }
            playerMP.b(chatMessage)
        }
    }

    companion object {

        private val FORMAT = Pattern.compile("((?<!&)&[0-9a-fk-or])+")

        private fun parseStyle(text: String): ChatModifier {
            var style = ChatModifier()
            val length = text.length
            var i = 1
            while (i < length) {
                when (text[i]) {
                    '0' -> style.setColor(EnumChatFormat.BLACK)
                    '1' -> style.setColor(EnumChatFormat.DARK_BLUE)
                    '2' -> style.setColor(EnumChatFormat.DARK_GREEN)
                    '3' -> style.setColor(EnumChatFormat.DARK_AQUA)
                    '4' -> style.setColor(EnumChatFormat.DARK_RED)
                    '5' -> style.setColor(EnumChatFormat.DARK_PURPLE)
                    '6' -> style.setColor(EnumChatFormat.GOLD)
                    '7' -> style.setColor(EnumChatFormat.GRAY)
                    '8' -> style.setColor(EnumChatFormat.DARK_GRAY)
                    '9' -> style.setColor(EnumChatFormat.BLUE)
                    'a' -> style.setColor(EnumChatFormat.GREEN)
                    'b' -> style.setColor(EnumChatFormat.AQUA)
                    'c' -> style.setColor(EnumChatFormat.RED)
                    'd' -> style.setColor(EnumChatFormat.LIGHT_PURPLE)
                    'e' -> style.setColor(EnumChatFormat.YELLOW)
                    'f' -> style.setColor(EnumChatFormat.WHITE)
                    'k' -> style.setRandom(true)
                    'l' -> style.setBold(true)
                    'm' -> style.setStrikethrough(true)
                    'n' -> style.setUnderline(true)
                    'o' -> style.setItalic(true)
                    else -> style = ChatModifier()
                }
                i += 2
            }
            return style
        }

        @JvmOverloads
        fun format(text: String, ca: EnumClickAction? = null, cv: String? = null, ha: EnumHoverAction? = null, hv: String? = null): IChatBaseComponent {
            val matcher = FORMAT.matcher(text)
            val component = ChatComponentText("")
            var head = 0
            var style = ChatModifier()
            while (matcher.find()) {
                component.addSibling(ChatComponentText(text.substring(head, matcher.start()).replace("&&".toRegex(), "&")).setChatModifier(style))
                style = parseStyle(matcher.group())
                head = matcher.end()
            }
            component.addSibling(ChatComponentText(text.substring(head).replace("&&".toRegex(), "&")).setChatModifier(style))
            if (ca != null && cv != null) {
                component.chatModifier.setChatClickable(ChatClickable(ca, cv))
            }
            if (ha != null && hv != null) {
                component.chatModifier.a(ChatHoverable(ha, format(hv)))
            }
            return component
        }
    }

}
