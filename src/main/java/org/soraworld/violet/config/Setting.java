package org.soraworld.violet.config;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Setting {
    /**
     * The path this setting is located at
     *
     * @return The path
     */
    String value() default "";

    /**
     * The default comment associated with this configuration node
     * This will be applied to any comment-capable configuration loader
     *
     * @return The comment
     */
    String comment() default "";

    boolean immortal() default false;

}
