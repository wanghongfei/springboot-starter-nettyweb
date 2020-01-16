package cn.fh.springboot.starter.nettyweb.validation;

import cn.fh.springboot.starter.nettyweb.annotation.validation.CollectionNotEmpty;
import cn.fh.springboot.starter.nettyweb.error.ValidationException;

import java.util.Collection;

/**
 * Created by wanghongfei on 2020/1/16.
 */
public class CollectionNotEmptyValidator implements WebValidator<Collection<?>, CollectionNotEmpty> {
    @Override
    public void validate(Collection<?> argument, CollectionNotEmpty anntation) {
        if (null == argument || argument.isEmpty()) {
            throw new ValidationException(anntation.message());
        }
    }

    @Override
    public boolean isSupported(Class<?> type) {
        return type == Collection.class;
    }
}
