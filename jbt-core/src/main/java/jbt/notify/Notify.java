package jbt.notify;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明Notify
 *
 * @author jinfeng.hu  @date 2024-04-13
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Notify {
    String value() default "";
    // 通常用于子类关闭父类的注解
    boolean enabled() default true;
}
