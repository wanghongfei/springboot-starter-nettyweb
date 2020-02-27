package com.wanghongfei.springboot.starter.nettyweb.api.component;

import com.wanghongfei.springboot.starter.nettyweb.annotation.HttpApi;
import com.wanghongfei.springboot.starter.nettyweb.network.handler.AroundRequestHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by wanghongfei on 2020/2/27.
 */
@HttpApi(path = "/around", paramType = DemoRequest.class)
public class DemoAroundApi implements AroundRequestHandler<DemoRequest, String> {
    @Override
    public String serveRequest(DemoRequest request) {
        System.out.println("middle");
        return "ok";
    }

    @Override
    public boolean before(DemoRequest demoRequest, ChannelHandlerContext context) {
        System.out.println("before");

        // 可直接写入响应数据
        // context.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer("break", StandardCharsets.UTF_8)));

        // false表示中断处理流程; 如果返回false, 务必保证已经通过context返回了适当的数据
        // return false
        return true;
    }

    @Override
    public void after(DemoRequest demoRequest, String retObject) {
        System.out.println("after");

    }
}
