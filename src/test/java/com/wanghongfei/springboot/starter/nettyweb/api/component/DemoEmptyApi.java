package com.wanghongfei.springboot.starter.nettyweb.api.component;

import com.wanghongfei.springboot.starter.nettyweb.annotation.HttpApi;
import com.wanghongfei.springboot.starter.nettyweb.network.handler.RawRequestHandler;

/**
 * Created by wanghongfei on 2020/2/26.
 */
@HttpApi(path = "/raw", method = "GET")
public class DemoEmptyApi implements RawRequestHandler<String> {
    @Override
    public String serveRequest() {
        return "raw";
    }
}
