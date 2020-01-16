package cn.fh.springboot.starter.nettyweb.validation;

import cn.fh.springboot.starter.nettyweb.annotation.validation.StringLen;
import cn.fh.springboot.starter.nettyweb.error.ValidationException;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by wanghongfei on 2020/1/16.
 */
public class StringLenValidator implements WebValidator<String, StringLen> {
    @Override
    public void validate(String argument, StringLen anntation) {
        int argLen = StringUtils.length(argument);
        if (argLen < anntation.min() || argLen > anntation.max()) {
            throw new ValidationException(anntation.message());
        }
    }

    @Override
    public boolean isSupported(Class<?> type) {
        return type == String.class;
    }
}
