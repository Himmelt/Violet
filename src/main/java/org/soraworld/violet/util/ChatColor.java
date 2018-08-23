package org.soraworld.violet.util;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 颜色枚举.
 */
public enum ChatColor {
    /**
     * 黑色.
     */
    BLACK('0'),
    /**
     * 深蓝.
     */
    DARK_BLUE('1'),
    /**
     * 深绿.
     */
    DARK_GREEN('2'),
    /**
     * 青色.
     */
    DARK_AQUA('3'),
    /**
     * 深红.
     */
    DARK_RED('4'),
    /**
     * 紫色.
     */
    DARK_PURPLE('5'),
    /**
     * 金色.
     */
    GOLD('6'),
    /**
     * 灰色.
     */
    GRAY('7'),
    /**
     * 深灰.
     */
    DARK_GRAY('8'),
    /**
     * 蓝色.
     */
    BLUE('9'),
    /**
     * 绿色.
     */
    GREEN('a'),
    /**
     * 天蓝.
     */
    AQUA('b'),
    /**
     * 鲜红.
     */
    RED('c'),
    /**
     * 浅紫.
     */
    LIGHT_PURPLE('d'),
    /**
     * 黄色.
     */
    YELLOW('e'),
    /**
     * 白色.
     */
    WHITE('f'),
    /**
     * 随机.
     */
    MAGIC('k'),
    /**
     * 粗体.
     */
    BOLD('l'),
    /**
     * 删除线.
     */
    STRIKETHROUGH('m'),
    /**
     * 下划线.
     */
    UNDERLINE('n'),
    /**
     * 斜体.
     */
    ITALIC('o'),
    /**
     * 重置.
     */
    RESET('r');

    private final String string;
    /**
     * 颜色字符.
     */
    public static final char COLOR_CHAR = '\u00A7';
    /**
     * 双颜色字符串.
     */
    public static final String D_COLOR_CHAR = "" + COLOR_CHAR + COLOR_CHAR;
    /**
     * 格式字符正则表达式.
     */
    public static final Pattern COLOR_PATTERN = Pattern.compile("(&[&|0-9a-fk-or])+");
    /**
     * 真实格式字符正则表达式.
     */
    public static final Pattern REAL_COLOR = Pattern.compile("(\u00A7[0-9a-fk-or])+");

    ChatColor(char code) {
        this.string = new String(new char[]{COLOR_CHAR, code});
    }

    /**
     * 颜色化字符串.
     *
     * @param text 原始文本
     * @return 处理结果
     */
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
