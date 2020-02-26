package com.wanghongfei.springboot.starter.nettyweb.annotation.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wanghongfei on 2020/1/16.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringLen {
    boolean canNull() default false;

    int min();
    int max();
    String message() default "";
}
