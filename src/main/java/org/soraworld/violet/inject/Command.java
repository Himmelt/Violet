package org.soraworld.violet.inject;

import java.lang.annotation.*;

/**
 * @author Himmelt
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Command {
    String name() default "";

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
