package com.wanghongfei.springboot.starter.nettyweb.api.component;

import com.wanghongfei.springboot.starter.nettyweb.annotation.HttpApi;
import com.wanghongfei.springboot.starter.nettyweb.network.RequestHandler;

/**
 * Created by wanghongfei on 2020/2/25.
 */
@HttpApi(path = "/", paramType = DemoRequest.class, method = "GET")
public class DemoGetApi implements RequestHandler<DemoRequest, String> {
    @Override
    public String serveRequest(DemoRequest request) {
        return "hello, " + request.getName() + ", " + request.getId();
    }
}
