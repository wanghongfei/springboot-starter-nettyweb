package cn.wanghongfei.springboot.starter.nettyweb.api;

import cn.wanghongfei.springboot.starter.nettyweb.annotation.HttpApi;
import cn.wanghongfei.springboot.starter.nettyweb.network.RawRequestHandler;

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
