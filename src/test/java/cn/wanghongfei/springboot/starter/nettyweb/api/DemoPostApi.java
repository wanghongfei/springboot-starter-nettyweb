package cn.wanghongfei.springboot.starter.nettyweb.api;

import cn.wanghongfei.springboot.starter.nettyweb.annotation.HttpApi;
import cn.wanghongfei.springboot.starter.nettyweb.network.RequestHandler;

/**
 * Created by wanghongfei on 2020/2/25.
 */
@HttpApi(path = "/post", paramType = DemoRequest.class, method = "POST")
public class DemoPostApi implements RequestHandler<DemoRequest, String> {
    @Override
    public String serveRequest(DemoRequest request) {
        return "post, " + request.getName();
    }
}
