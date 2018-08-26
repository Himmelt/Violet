package org.soraworld.violet.command;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Sub {
    String path() default "";

    String perm() default "";

    String[] aliases() default {};

    String usage() default "";
}
