package cn.wanghongfei.springboot.starter.nettyweb.validation;

import cn.wanghongfei.springboot.starter.nettyweb.annotation.validation.StringLen;
import cn.wanghongfei.springboot.starter.nettyweb.error.ValidationException;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by wanghongfei on 2020/1/16.
 */
public class StringLenValidator implements WebValidator<String, StringLen> {
    @Override
    public void validate(String argument, StringLen anntation) {
        if (anntation.canNull() && null == argument) {
            return;
        }

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
