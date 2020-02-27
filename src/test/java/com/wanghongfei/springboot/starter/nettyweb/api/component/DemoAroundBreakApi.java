package com.wanghongfei.springboot.starter.nettyweb.api.component;

import com.wanghongfei.springboot.starter.nettyweb.annotation.HttpApi;
import com.wanghongfei.springboot.starter.nettyweb.network.handler.AroundRequestHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;

/**
 * Created by wanghongfei on 2020/2/27.
 */
@HttpApi(path = "/around/break", paramType = DemoRequest.class)
public class DemoAroundBreakApi implements AroundRequestHandler<DemoRequest, String> {
    @Override
    public String serveRequest(DemoRequest request) {
        System.out.println("middle");
        return "ok";
    }

    @Override
    public boolean before(DemoRequest demoRequest, ChannelHandlerContext context) {
        System.out.println("before");
        context.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer("break", StandardCharsets.UTF_8)));

        return false;
    }

    @Override
    public void after(DemoRequest demoRequest, String retObject) {
        System.out.println("after");

    }
}
