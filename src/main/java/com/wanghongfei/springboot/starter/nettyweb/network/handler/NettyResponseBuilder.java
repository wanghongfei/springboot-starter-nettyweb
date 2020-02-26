package com.wanghongfei.springboot.starter.nettyweb.network.handler;

import com.wanghongfei.springboot.starter.nettyweb.network.vo.CommonResponse;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;

/**
 * Created by wanghongfei on 2020/1/22.
 */
public class NettyResponseBuilder {
    protected FullHttpResponse buildHttpResponse(Object data, String message, int code, Logger log, String prefix, Long uid) {
        Object responseObject = buildResponseObject(data, message, code);

        String json = JSON.toJSONString(responseObject);

        log.info("{} response for {}: {}", prefix, uid, json);

        FullHttpResponse httpResponse =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(json, StandardCharsets.UTF_8));
        httpResponse.headers().add("Content-Type", "application/json;charset=utf8");

        modifyHeader(httpResponse.headers());

        return httpResponse;
    }

    /**
     * 子类可以重写此方法来提供自定义返回格式
     * @param data 业务返回的对象
     *
     * @return 返回的对象会被JSON序列化发送给客户端
     */
    protected Object buildResponseObject(Object data, String message, int code) {
        CommonResponse response = new CommonResponse();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);

        return response;
    }

    /**
     * 子类重写此方法来修改响应头
     * @param headers
     */
    protected void modifyHeader(HttpHeaders headers) {
    }
}
