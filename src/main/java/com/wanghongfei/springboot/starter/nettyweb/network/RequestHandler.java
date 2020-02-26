package com.wanghongfei.springboot.starter.nettyweb.network;

import io.netty.handler.codec.http.HttpHeaders;

/**
 * 所有HTTP请求处理器都要实现此接口
 *
 * @param <T> 请求参数类型
 * @param <R> 请求处理方法的返回类型
 */
public interface RequestHandler<T, R> {
    /**
     * 请求处理逻辑;
     *
     * @param request 请求参数对象
     * @return 请求响应对象
     */
    R serveRequest(T request);

    /**
     * 给用户一个修改响应头的机会;
     *
     * @param headers 响应头
     * @param data {@link #serveRequest(Object)}的返回值
     */
    default void modifyHeader(HttpHeaders headers, R data) {

    }
}
