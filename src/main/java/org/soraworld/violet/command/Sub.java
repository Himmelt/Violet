package org.soraworld.violet.command;

import java.lang.annotation.*;

/**
 * 子命令注解.
 * 只有注解签名为 {@code public static void (SpigotManager,CommandSender,Paths);}
 * 和 {@code public static void (SpongeManager,CommandSource,Paths);} 的方法才会被提取.
 * @author Himmelt
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Sub {

    String parent() default "";

    /**
     * 命令树, 用 . 分隔
     * 大写字母会被转换为小写字母.
     *
     * @return 命令树
     */
    String path() default "";

    /**
     * 是否为抽象命令.
     * 抽象命令只是为命令提供基本信息，不具备执行功能。
     * 要注意顺序，后注册的命令会覆盖前面相同路径的命令。
     *
     * @return 是否抽象命令
     */
    boolean virtual() default false;

    /**
     * 权限.
     * 此处权限会经过配置文件的权限映射表处理.
     *
     * @return 权限
     */
    String perm() default "";

    /**
     * 是否仅玩家可执行.
     *
     * @return 是否仅玩家执行
     */
    boolean onlyPlayer() default false;

    /**
     * 别名数组.
     * 请不要使用 空格 标点 等字符 ！！！
     *
     * @return 别名数组
     */
    String[] aliases() default {};

    /**
     * Tab 补全候选列表.
     *
     * @return Tab 补全候选列表
     */
    String[] tabs() default {};

    /**
     * 用法.
     *
     * @return 用法
     */
    String usage() default "";
}
