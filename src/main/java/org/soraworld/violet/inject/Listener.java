package org.soraworld.violet.inject;

import java.lang.annotation.*;

/**
 * @author Himmelt
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Listener {
    String mcversion() default "";
}
