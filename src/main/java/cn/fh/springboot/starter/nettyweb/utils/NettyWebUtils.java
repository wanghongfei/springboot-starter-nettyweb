package cn.fh.springboot.starter.nettyweb.utils;

import cn.fh.springboot.starter.nettyweb.network.vo.CommonResponse;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;

/**
 * Created by wanghongfei on 2019/11/4.
 */
public class NettyWebUtils {
    public static FullHttpResponse buildErrResponse(String message, HttpResponseStatus status, Logger log, String prefix, Long uid) {
        CommonResponse response = new CommonResponse();
        response.setCode(status.code());
        response.setMessage(message);

        String json = JSON.toJSONString(response);

        log.info("{} response for {}: {}", uid, prefix, json);

        FullHttpResponse httpResponse =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(json, StandardCharsets.UTF_8));
        httpResponse.headers().add("Content-Type", "application/json;charset=utf8");

        return httpResponse;
    }

    public static FullHttpResponse buildOkResponse(Object data, Logger log, String prefix, Long uid) {
        CommonResponse response = new CommonResponse();
        response.setCode(0);
        response.setMessage("ok");
        response.setData(data);

        String json = JSON.toJSONString(response);

        log.info("{} response for {}: {}", uid, prefix, json);

        FullHttpResponse httpResponse =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(json, StandardCharsets.UTF_8));
        httpResponse.headers().add("Content-Type", "application/json;charset=utf8");

        return httpResponse;
    }
}
