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

    // TODO 单独文件，单独异步加载
    boolean separate() default false;
}
