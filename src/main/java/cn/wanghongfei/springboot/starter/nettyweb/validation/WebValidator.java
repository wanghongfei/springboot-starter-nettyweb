package cn.wanghongfei.springboot.starter.nettyweb.validation;

import java.lang.annotation.Annotation;

/**
 * Created by wanghongfei on 2020/1/16.
 */
public interface WebValidator<T_OBJ, T_ANN extends Annotation> {
    void validate(T_OBJ argument, T_ANN anntation);

    boolean isSupported(Class<?> type);
}
