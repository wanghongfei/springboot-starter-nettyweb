package com.wanghongfei.springboot.starter.nettyweb.validation;

import com.wanghongfei.springboot.starter.nettyweb.annotation.validation.StringCandidate;
import com.wanghongfei.springboot.starter.nettyweb.error.ValidationException;

/**
 * Created by wanghongfei on 2020/1/16.
 */
public class StringCandidateValidator implements WebValidator<String, StringCandidate> {
    @Override
    public void validate(String argument, StringCandidate anntation) {
        if (anntation.canNull() && null == argument) {
            return;
        }

        if (null == argument) {
            throw new ValidationException(anntation.message());
        }

        String[] candidates = anntation.candidates();
        if (0 == candidates.length) {
            return;
        }

        for (String candidate : candidates) {
            if (argument.equals(candidate)) {
                return;
            }
        }

        throw new ValidationException(anntation.message());
    }

    @Override
    public boolean isSupported(Class<?> type) {
        return type == String.class;
    }
}
