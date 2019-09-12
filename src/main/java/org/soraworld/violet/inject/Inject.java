package org.soraworld.violet.inject;

import java.lang.annotation.*;


/**
 * 静态注入的字段不能在静态初始化块中用，类加载时尚未注入.
 * @author Himmelt
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Documented
public @interface Inject {
}
