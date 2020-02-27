package com.wanghongfei.springboot.starter.nettyweb.api.component;

import com.wanghongfei.springboot.starter.nettyweb.annotation.validation.NumberSize;
import com.wanghongfei.springboot.starter.nettyweb.annotation.validation.StringCandidate;
import com.wanghongfei.springboot.starter.nettyweb.annotation.validation.Validation;
import lombok.Data;

/**
 * Created by wanghongfei on 2020/2/27.
 */
@Validation
@Data
public class DemoValidationRequest {
    @StringCandidate(candidates = {"neo", "bruce"}, message = "invalid name")
    private String name;

    @NumberSize(min = 1, max = 100, message = "invalid age")
    private Long age;
}
