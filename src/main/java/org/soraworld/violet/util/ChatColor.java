package org.soraworld.violet.util;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ChatColor {
    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    MAGIC('k'),
    BOLD('l'),
    STRIKETHROUGH('m'),
    UNDERLINE('n'),
    ITALIC('o'),
    RESET('r');

    private final String string;
    public static final char COLOR_CHAR = '\u00A7';
    public static final String D_COLOR_CHAR = "" + COLOR_CHAR + COLOR_CHAR;
    public static final Pattern COLOR_PATTERN = Pattern.compile("(&[&|0-9a-fk-or])+");
    public static final Pattern REAL_COLOR = Pattern.compile("(\u00A7[0-9a-fk-or])+");

    ChatColor(char code) {
        this.string = new String(new char[]{COLOR_CHAR, code});
    }

    public static String colorize(@Nonnull String text) {
        Matcher matcher = COLOR_PATTERN.matcher(text);
        StringBuilder builder = new StringBuilder();
        int head = 0;
        while (matcher.find()) {
            String group = matcher.group().replace('&', COLOR_CHAR).replace(D_COLOR_CHAR, "&");
            int start = matcher.start();
            builder.append(text, head, start).append(group);
            head = matcher.end();
        }
        builder.append(text, head, text.length());
        return builder.toString();
    }

    public String toString() {
        return string;
    }
}
