package cn.wanghongfei.springboot.starter.nettyweb.validation;

import cn.wanghongfei.springboot.starter.nettyweb.annotation.validation.NotNull;
import cn.wanghongfei.springboot.starter.nettyweb.error.ValidationException;

/**
 * Created by wanghongfei on 2020/1/16.
 */
public class NotNullValidator implements WebValidator<Object, NotNull> {
    @Override
    public void validate(Object argument, NotNull anntation) {
        if (null == argument) {
            throw new ValidationException(anntation.message());
        }
    }

    @Override
    public boolean isSupported(Class<?> type) {
        return true;
    }
}
