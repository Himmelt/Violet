package org.soraworld.violet.inject;

import java.lang.annotation.*;


/**
 * 选择性加载的类不应使用该注解，此注解会在插件加载时触发类加载。
 *
 * @author Himmelt
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Documented
public @interface Inject {
    String mcversion() default "";
}
