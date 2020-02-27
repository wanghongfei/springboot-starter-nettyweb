package com.wanghongfei.springboot.starter.nettyweb.network.handler;

import io.netty.channel.ChannelHandlerContext;

/**
 * 支持在业务逻辑之前、之后添加自定义逻辑的请求处理器, 类似于Filter
 *
 * Created by wanghongfei on 2020/2/27.
 */
public interface AroundRequestHandler<T_PARAM, T_RETURN> extends RequestHandler<T_PARAM, T_RETURN> {
    /**
     * 前置方法, 在执行业务逻辑之前调用
     * @param param 业务参数
     * @param context Netty Channel Handler上下文的引用, 可以直接操作写入响应信息
     *
     * @return true表示继续后续逻辑, false表示终止请求
     */
    default boolean before(T_PARAM param, ChannelHandlerContext context) {
        return true;
    }

    /**
     * 后置方法, 在业务执行完后调用
     * @param param 业务参数
     * @param retObject 业务的返回对象
     */
    default void after(T_PARAM param, T_RETURN retObject) {

    }
}
