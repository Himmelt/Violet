package org.soraworld.violet.inject;

import java.lang.annotation.*;

/**
 * @author Himmelt
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Config {
    String id();

    boolean separate() default false;

    String mcversion() default "";
}
