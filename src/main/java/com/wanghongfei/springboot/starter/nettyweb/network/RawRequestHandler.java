package com.wanghongfei.springboot.starter.nettyweb.network;

/**
 * Created by wanghongfei on 2020/1/21.
 */
public interface RawRequestHandler<R> extends RequestHandler<Void, R> {
    @Override
    default R serveRequest(Void request) {
        return null;
    }

    R serveRequest();
}
