package cn.fh.springboot.starter.nettyweb.validation;

import cn.fh.springboot.starter.nettyweb.annotation.validation.StringCandidate;
import cn.fh.springboot.starter.nettyweb.error.ValidationException;

/**
 * Created by wanghongfei on 2020/1/16.
 */
public class StringCandidateValidator implements WebValidator<String, StringCandidate> {
    @Override
    public void validate(String argument, StringCandidate anntation) {
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
