package cn.wanghongfei.springboot.starter.nettyweb.api;

import cn.wanghongfei.springboot.starter.nettyweb.network.handler.NettyResponseBuilder;
import lombok.Data;

/**
 * Created by wanghongfei on 2020/2/26.
 */
//@Component
public class MyResponseBuilder extends NettyResponseBuilder {
    @Override
    protected Object buildResponseObject(Object data, String message, int code) {
        MyResponse response = new MyResponse();
        response.setData(data);

        return response;
    }

    @Data
    public static class MyResponse {
        private Object data;
    }
}
