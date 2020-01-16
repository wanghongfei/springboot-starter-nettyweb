package cn.fh.springboot.starter.nettyweb.validation;

import cn.fh.springboot.starter.nettyweb.annotation.validation.CollectionNotEmpty;
import cn.fh.springboot.starter.nettyweb.annotation.validation.NotNull;
import cn.fh.springboot.starter.nettyweb.annotation.validation.NumberSize;
import cn.fh.springboot.starter.nettyweb.annotation.validation.StringCandidate;
import cn.fh.springboot.starter.nettyweb.annotation.validation.StringLen;
import cn.fh.springboot.starter.nettyweb.annotation.validation.StringReg;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanghongfei on 2020/1/16.
 */
public class ValidatorMapping {
    private Map<Class<?>, WebValidator<?, ?>> typeValidatorMap = new HashMap<>();

    public ValidatorMapping() {
        typeValidatorMap.put(StringLen.class, new StringLenValidator());
        typeValidatorMap.put(StringCandidate.class, new StringCandidateValidator());
        typeValidatorMap.put(NumberSize.class, new NumberSizeValidator());
        typeValidatorMap.put(CollectionNotEmpty.class, new CollectionNotEmptyValidator());
        typeValidatorMap.put(NotNull.class, new NotNullValidator());
        typeValidatorMap.put(StringReg.class, new StringRegValidator());
    }

    public WebValidator<?, ?> getValidator(Class<?> argType) {
        return typeValidatorMap.get(argType);
    }

    public void invokeValidator(Annotation an, Object targetParam, Class<?> paramType) {
        WebValidator validator = getValidator(an.annotationType());
        if (null == validator) {
            return;
        }

        if (!validator.isSupported(paramType)) {
            throw new IllegalStateException("annatation " + an + " does not support object type " + paramType);
        }

        validator.validate(targetParam, an);
    }
}
