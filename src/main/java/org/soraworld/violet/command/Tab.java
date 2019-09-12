package org.soraworld.violet.command;

import java.lang.annotation.*;

/**
 * @author Himmelt
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Tab {
    String parent() default "";

    /**
     * 命令树, 用 . 分隔
     * 大写字母会被转换为小写字母.
     *
     * @return 命令树
     */
    String path() default "";
}
