package com.wanghongfei.springboot.starter.nettyweb.api.component;

import com.wanghongfei.springboot.starter.nettyweb.annotation.HttpApi;
import com.wanghongfei.springboot.starter.nettyweb.network.RequestHandler;

/**
 * Created by wanghongfei on 2020/2/27.
 */
@HttpApi(path = "/validation", paramType = DemoValidationRequest.class)
public class DemoValidationApi implements RequestHandler<DemoValidationRequest, String> {
    @Override
    public String serveRequest(DemoValidationRequest request) {
        return "ok";
    }
}
