package org.soraworld.violet.inject;

import java.lang.annotation.*;

/**
 * @author Himmelt
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Tab {
    String path() default "";
}
