package cn.fh.springboot.starter.nettyweb.validation;

import cn.fh.springboot.starter.nettyweb.annotation.validation.StringLen;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanghongfei on 2020/1/16.
 */
public class ValidatorMapping {
    private Map<Class, WebValidator> typeValidatorMap = new HashMap<>();

    public ValidatorMapping() {
        typeValidatorMap.put(StringLen.class, new StringLenValidator());
    }

    public WebValidator getValidator(Class argType) {
        return typeValidatorMap.get(argType);
    }

    public void invokeValidator(Annotation an, Object targetParam) {
        WebValidator validator = getValidator(an.annotationType());
        if (null == validator) {
            return;
        }

        validator.validate(targetParam, an);
    }
}
