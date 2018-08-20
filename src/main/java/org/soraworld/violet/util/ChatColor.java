package org.soraworld.violet.util;

import java.util.HashMap;
import java.util.Map;

import static org.soraworld.violet.Violet.COLOR_CHAR;

public enum ChatColor {
    BLACK('0', 0x00),
    DARK_BLUE('1', 0x1),
    DARK_GREEN('2', 0x2),
    DARK_AQUA('3', 0x3),
    DARK_RED('4', 0x4),
    DARK_PURPLE('5', 0x5),
    GOLD('6', 0x6),
    GRAY('7', 0x7),
    DARK_GRAY('8', 0x8),
    BLUE('9', 0x9),
    GREEN('a', 0xA),
    AQUA('b', 0xB),
    RED('c', 0xC),
    LIGHT_PURPLE('d', 0xD),
    YELLOW('e', 0xE),
    WHITE('f', 0xF),
    MAGIC('k', 0x10),
    BOLD('l', 0x11),
    STRIKETHROUGH('m', 0x12),
    UNDERLINE('n', 0x13),
    ITALIC('o', 0x14),
    RESET('r', 0x15);

    private final int id;
    private final char code;
    private final String string;
    private final static Map<Integer, ChatColor> BY_ID = new HashMap<>();
    private final static Map<Character, ChatColor> BY_CHAR = new HashMap<>();

    ChatColor(char code, int id) {
        this.code = code;
        this.id = id;
        this.string = new String(new char[]{COLOR_CHAR, code});
    }

    public int getId() {
        return id;
    }

    public char getCode() {
        return code;
    }

    public String toString() {
        return string;
    }
}
