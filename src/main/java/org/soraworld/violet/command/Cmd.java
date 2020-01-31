package org.soraworld.violet.command;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Documented
public @interface Cmd {

    String plugin() default "";

    String name() default "";

    String perm() default "";

    boolean admin() default false;

    boolean ingame() default false;

    String[] aliases() default {};

    String[] tabs() default {};

    String usage() default "";

    String description() default "";
}
