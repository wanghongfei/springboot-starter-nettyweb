package com.wanghongfei.springboot.starter.nettyweb.validation;

import com.wanghongfei.springboot.starter.nettyweb.annotation.validation.StringReg;
import com.wanghongfei.springboot.starter.nettyweb.error.ValidationException;

import java.util.regex.Pattern;

/**
 * Created by wanghongfei on 2020/1/16.
 */
public class StringRegValidator implements WebValidator<String, StringReg> {
    @Override
    public void validate(String argument, StringReg anntation) {
        if (anntation.canNull() && null == argument) {
            return;
        }

        if (null == argument) {
            throw new ValidationException(anntation.message());
        }

        boolean match = Pattern.matches(anntation.pattern(), argument);
        if (!match) {
            throw new ValidationException(anntation.message());
        }
    }

    @Override
    public boolean isSupported(Class<?> type) {
        return type == String.class;
    }
}
