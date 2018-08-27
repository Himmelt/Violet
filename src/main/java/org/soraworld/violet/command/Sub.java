package org.soraworld.violet.command;

import java.lang.annotation.*;

/**
 * The interface Sub.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Sub {
    /**
     * Paths string [ ].
     *
     * @return the string [ ]
     */
    String[] paths() default {};

    /**
     * 权限.
     * 如果填 "admin" 则 使用 Manager 的 defaultAdmin()
     *
     * @return 权限
     */
    String perm() default "";

    /**
     * Only player boolean.
     *
     * @return the boolean
     */
    boolean onlyPlayer() default false;

    /**
     * Aliases string [ ].
     *
     * @return the string [ ]
     */
    String[] aliases() default {};

    /**
     * Tabs string [ ].
     *
     * @return the string [ ]
     */
    String[] tabs() default {};

    /**
     * Usage string.
     *
     * @return the string
     */
    String usage() default "";
}
