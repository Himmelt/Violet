package org.soraworld.violet.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 颜色枚举 和 处理工具.
 * @author Himmelt
 */
public enum ChatColor {
    /**
     * 黑色.
     */
    BLACK('0', "black"),
    /**
     * 深蓝.
     */
    DARK_BLUE('1', "dark_blue"),
    /**
     * 深绿.
     */
    DARK_GREEN('2', "dark_green"),
    /**
     * 青色.
     */
    DARK_AQUA('3', "dark_aqua"),
    /**
     * 深红.
     */
    DARK_RED('4', "dark_red"),
    /**
     * 紫色.
     */
    DARK_PURPLE('5', "dark_purple"),
    /**
     * 金色.
     */
    GOLD('6', "gold"),
    /**
     * 灰色.
     */
    GRAY('7', "gray"),
    /**
     * 深灰.
     */
    DARK_GRAY('8', "dark_gray"),
    /**
     * 蓝色.
     */
    BLUE('9', "blue"),
    /**
     * 绿色.
     */
    GREEN('a', "green"),
    /**
     * 天蓝.
     */
    AQUA('b', "aqua"),
    /**
     * 鲜红.
     */
    RED('c', "red"),
    /**
     * 浅紫.
     */
    LIGHT_PURPLE('d', "light_purple"),
    /**
     * 黄色.
     */
    YELLOW('e', "yellow"),
    /**
     * 白色.
     */
    WHITE('f', "white"),
    /**
     * 随机.
     */
    MAGIC('k', "obfuscated"),
    /**
     * 粗体.
     */
    BOLD('l', "bold"),
    /**
     * 删除线.
     */
    STRIKETHROUGH('m', "strikethrough"),
    /**
     * 下划线.
     */
    UNDERLINE('n', "underline"),
    /**
     * 斜体.
     */
    ITALIC('o', "italic"),
    /**
     * 重置.
     */
    RESET('r', "reset");

    private final char code;
    private final String name;
    private final String string;

    /**
     * 真实颜色 字符.
     */
    public static final char TRUE_COLOR_CHAR = '\u00A7';
    /**
     * 虚拟颜色 字符.
     */
    public static final char FAKE_COLOR_CHAR = '&';
    /**
     * 真实颜色 字符串.
     */
    public static final String TRUE_COLOR_STRING = "\u00A7";
    /**
     * 虚拟颜色 字符串.
     */
    public static final String FAKE_COLOR_STRING = "&";

    /**
     * 真实格式 字符串 正则表达式.
     */
    public static final Pattern TRUE_COLOR_PATTERN = Pattern.compile("(?i)\u00A7[0-9a-fk-or]");
    /**
     * 虚拟格式 字符串 正则表达式.
     */
    public static final Pattern FAKE_COLOR_PATTERN = Pattern.compile("(?i)&[0-9a-fk-or]");
    /**
     * 虚拟和实际格式 字符串 正则表达式.
     */
    public static final Pattern ALL_COLOR_PATTERN = Pattern.compile("(?i)[&|\u00A7][0-9a-fk-or]");
    /**
     * 虚拟格式 前导字符 正则表达式.
     */
    public static final Pattern FAKE_HEAD_PATTERN = Pattern.compile("(?i)&(?=[0-9a-fk-or])");
    /**
     * 真实格式 前导字符 正则表达式.
     */
    public static final Pattern TRUE_HEAD_PATTERN = Pattern.compile("(?i)\u00A7(?=[0-9a-fk-or])");

    private static final Map<Integer, ChatColor> BY_ID = new HashMap<>();
    private static final Map<Character, ChatColor> BY_CHAR = new HashMap<>();
    private static final Map<String, ChatColor> BY_NAME = new HashMap<>();

    static {
        for (ChatColor colour : values()) {
            BY_ID.put(colour.ordinal(), colour);
            BY_CHAR.put(colour.code, colour);
            BY_NAME.put(colour.name, colour);
        }
    }

    ChatColor(char code, String name) {
        this.code = code;
        this.name = name;
        this.string = new String(new char[]{TRUE_COLOR_CHAR, code});
    }

    /**
     * 真实颜色化字符串.<br>
     * {@code & -> §}
     *
     * @param text 原始文本
     * @return 处理结果
     */
    @NotNull
    public static String colorize(@NotNull String text) {
        return FAKE_HEAD_PATTERN.matcher(text).replaceAll(TRUE_COLOR_STRING);
    }

    /**
     * 虚拟颜色化字符串.<br>
     * {@code § -> &}
     *
     * @param text 原始文本
     * @return 处理结果
     */
    @NotNull
    public static String fakerize(@NotNull String text) {
        return TRUE_HEAD_PATTERN.matcher(text).replaceAll(FAKE_COLOR_STRING);
    }

    @Override
    public String toString() {
        return string;
    }

    /**
     * 移除字符串中所有的 真实颜色字符.
     *
     * @param text 原始文本
     * @return 处理结果
     */
    @NotNull
    public static String stripColor(@NotNull String text) {
        return TRUE_COLOR_PATTERN.matcher(text).replaceAll("");
    }

    /**
     * 移除字符串中所有的 虚拟颜色字符.
     *
     * @param text 原始文本
     * @return 处理结果
     */
    @NotNull
    public static String stripFakeColor(@NotNull String text) {
        return FAKE_COLOR_PATTERN.matcher(text).replaceAll("");
    }

    /**
     * 移除字符串中所有的 虚拟和真实 颜色字符.
     *
     * @param text 原始文本
     * @return 处理结果
     */
    @NotNull
    public static String stripAllColor(@NotNull String text) {
        return ALL_COLOR_PATTERN.matcher(text).replaceAll("");
    }

    public static ChatColor getById(int id) {
        return BY_ID.get(id);
    }

    public static ChatColor getByChar(char code) {
        return BY_CHAR.get(code);
    }

    public static ChatColor getByName(String name) {
        return BY_NAME.get(name);
    }
}
