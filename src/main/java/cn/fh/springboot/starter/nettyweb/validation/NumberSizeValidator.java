package cn.fh.springboot.starter.nettyweb.validation;

import cn.fh.springboot.starter.nettyweb.annotation.validation.NumberSize;
import cn.fh.springboot.starter.nettyweb.error.ValidationException;

/**
 * Created by wanghongfei on 2020/1/16.
 */
public class NumberSizeValidator implements WebValidator<Number, NumberSize> {
    @Override
    public void validate(Number argument, NumberSize anntation) {
        if (anntation.canNull() && null == argument) {
            return;
        }

        if (null == argument) {
            throw new ValidationException(anntation.message());
        }

        long num = argument.longValue();
        if (num < anntation.min() || num > anntation.max()) {
            throw new ValidationException(anntation.message());
        }
    }

    @Override
    public boolean isSupported(Class<?> type) {
        return type == Integer.class || type == Long.class || type == Short.class;
    }
}
