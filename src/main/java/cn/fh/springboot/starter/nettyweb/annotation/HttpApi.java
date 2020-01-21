package cn.fh.springboot.starter.nettyweb.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wanghongfei on 2019/11/11.
 */
@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpApi {
    /**
     * 以/开头的路径
     */
    String path() default "";

    /**
     * 指定接口参数对象;
     * 不传意为此接口不需要任何参数;
     */
    Class<?> paramType() default Void.class;

    /**
     * 请求方法,只支持GET, POST
     */
    String method() default "POST";
}
