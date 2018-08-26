package org.soraworld.violet.api;

import org.soraworld.violet.util.ChatColor;

import javax.annotation.Nonnull;

/**
 * 管理器接口.
 * 负责整个插件的主要管理工作.
 */
public interface IManager {

    /**
     * 默认控制台和聊天框的 抬头.
     * 该内容会被 xx_xx.lang 里的 chatHead 对应的值替换掉.
     * 只有在 语言文件 没有填写 chatHead 的时候才会使用该默认值.
     * 如果要使用颜色，请使用 {@link ChatColor}
     *
     * @return 默认抬头
     */
    @Nonnull
    default String defChatHead() {
        return "[" + getPlugin().getName() + "] ";
    }

    /**
     * 默认管理权限，用于给基础管理命令添加权限.
     *
     * @return 默认管理权限
     */
    @Nonnull
    default String defAdminPerm() {
        return getPlugin().getId() + ".admin";
    }

    /**
     * 默认聊天颜色.
     * 该颜色会放在所有抬头的前面.
     * 因此，该颜色只有在 默认抬头 和 chatHead 的值没有颜色代码时才有效果.
     *
     * @return 默认聊天颜色
     */
    @Nonnull
    ChatColor defChatColor();

    /**
     * 从文件加载配置.
     *
     * @return 是否成功
     */
    boolean load();

    /**
     * 保存配置到文件.
     *
     * @return 是否成功
     */
    boolean save();

    /**
     * 在配置加载之前执行.
     */
    default void beforeLoad() {
    }

    /**
     * 在配置加载之后执行.
     */
    default void afterLoad() {
    }

    /**
     * 获取当前语言.
     *
     * @return the lang
     */
    String getLang();

    /**
     * 设置语言.
     *
     * @param lang 待设置语言
     * @return 是否成功
     */
    boolean setLang(@Nonnull String lang);

    /**
     * 是否处于调试模式.
     *
     * @return 是否处于调试模式
     */
    boolean isDebug();

    /**
     * 开启/关闭调试模式.
     *
     * @param debug 模式
     */
    void setDebug(boolean debug);

    /**
     * 翻译字符串.
     *
     * @param key  键
     * @param args 参数
     * @return 翻译结果
     */
    String trans(@Nonnull String key, Object... args);

    /**
     * 向控制台输出文本.
     * 颜色请使用 {@link ChatColor}
     *
     * @param text 文本
     */
    void console(@Nonnull String text);

    /**
     * 向控制台输出翻译.
     *
     * @param key  键
     * @param args 参数
     */
    void consoleKey(@Nonnull String key, Object... args);

    /**
     * 广播文本.
     * 颜色请使用 {@link ChatColor}
     *
     * @param text 文本
     */
    void broadcast(@Nonnull String text);

    /**
     * 广播翻译.
     *
     * @param key  键
     * @param args 参数
     */
    void broadcastKey(@Nonnull String key, Object... args);

    /**
     * 打印输出文本.
     * 不支持颜色，原生输出.
     *
     * @param text 文本
     */
    void println(@Nonnull String text);

    /**
     * 获取插件实例.
     *
     * @return 获取
     */
    IPlugin getPlugin();
}
