package org.soraworld.violet.command;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Sub {
    String[] paths() default {};

    String perm() default "";

    boolean onlyPlayer() default false;

    String[] aliases() default {};

    String[] tabs() default {};

    String usage() default "";
}
