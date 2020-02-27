package com.wanghongfei.springboot.starter.nettyweb.network.handler;

/**
 * 不需要参数对象的请求处理器可实现此接口
 * Created by wanghongfei on 2020/1/21.
 */
public interface RawRequestHandler<R> extends RequestHandler<Void, R> {
    @Override
    default R serveRequest(Void request) {
        return null;
    }

    R serveRequest();
}
