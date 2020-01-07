package cn.fh.springboot.starter.nettyweb.network.handler;

import cn.fh.springboot.starter.nettyweb.autoconfig.NettyWebProp;
import cn.fh.springboot.starter.nettyweb.error.BizException;
import cn.fh.springboot.starter.nettyweb.error.ReadableException;
import cn.fh.springboot.starter.nettyweb.network.RequestHandler;
import cn.fh.springboot.starter.nettyweb.network.inject.InjectLoginToken;
import cn.fh.springboot.starter.nettyweb.utils.NettyWebUtils;
import cn.fh.springboot.starter.nettyweb.utils.SnowFlake;
import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wanghongfei on 2019/11/4.
 */
@ChannelHandler.Sharable
@Slf4j
public class NettyWebHandler extends ChannelInboundHandlerAdapter {
    private static String SERVICE_POOL_PATTERN = "nettyweb-service-pool-%d";

    @Autowired
    private NettyWebProp prop;

    private Map<String, Class> pathTypeMap = new HashMap<>();
    private Map<String, RequestHandler> pathServiceMap = new HashMap<>();

    private ThreadPoolExecutor servicePool;

    private AttributeKey<String> uriKey = AttributeKey.valueOf("uriKey");
    private AttributeKey<Long> uidKey = AttributeKey.valueOf("uidKey");



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        FullHttpRequest httpRequest = (FullHttpRequest) msg;

        // 取出path
        String path = httpRequest.uri();
        ctx.channel().attr(uriKey).set(path);

        // 取出service
        RequestHandler moonApi = pathServiceMap.get(path);
        if (null == moonApi) {
            throw new ReadableException("404");
        }

        // 取出参数对象类型
        Class paramType = pathTypeMap.get(path);
        if (null == paramType) {
            throw new ReadableException("lack argument for this path");
        }


        // 将body转成string
        String body = extractRequestBody(httpRequest);

        // 生成唯一标识
        Long uid = SnowFlake.genId();
        ctx.channel().attr(uidKey).set(uid);
        log.info("{} request for {}: {}", uid, path, body);

        // 反序列化
        Object paramObject = transBodyType(body, paramType);
        // 注入所需字段
        injectFields(paramObject, httpRequest);

        // 调用service
        servicePool.execute(() -> {
            try {
                Object retObj = moonApi.serveRequest(paramObject);
                ctx.writeAndFlush(NettyWebUtils.buildOkResponse(retObj, log, path, uid));

            } catch (Throwable e) {
                exceptionCaught(ctx, e);

            } finally {
                // 判断是不是persist connection
                String keepAlive = httpRequest.headers().get("Connection");
                if (!"Keep-Alive".equals(keepAlive)) {
                    ctx.close();
                }
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("get exception:", cause);

        HttpResponseStatus status;
        String msg;
        if (cause instanceof ReadableException) {
            // 业务异常
            status = HttpResponseStatus.BAD_REQUEST;
            msg = cause.getMessage();

        } else if (cause instanceof BizException) {
            status = HttpResponseStatus.BAD_REQUEST;
            msg = "biz error";

        } else {
            status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
            msg = "internal error";
        }

        String path = ctx.channel().attr(uriKey).get();
        Long uid = ctx.channel().attr(uidKey).get();

        ctx.writeAndFlush(NettyWebUtils.buildErrResponse(msg, status, log, path, uid));
        ctx.close();
    }

    public void registerService(String path, Class voType, RequestHandler service) {
        log.info("register service: {} -> {}, param type: {}", path, service.getClass(), voType);

        this.pathTypeMap.put(path, voType);
        this.pathServiceMap.put(path, service);
    }

    private String extractRequestBody(FullHttpRequest request) {
        return request.content().toString(StandardCharsets.UTF_8);
    }

    private void injectFields(Object param, FullHttpRequest httpRequest) {
        if (param instanceof InjectLoginToken) {
            // 注入loginToken
            InjectLoginToken injectLoginToken = (InjectLoginToken) param;
            String loginToken = httpRequest.headers().get(prop.getLoginTokenHeaderName());
            if (StringUtils.isEmpty(loginToken)) {
                throw new ReadableException("缺少token");
            }
            injectLoginToken.setLoginToken(loginToken);
        }
    }

    private Object transBodyType(String body, Class paramType) {
        try {
            return JSON.parseObject(body, paramType);

        } catch (Throwable e) {
            log.warn("failed to deserialize request json", e);
            throw new ReadableException("参数错误");
        }
    }

    @PostConstruct
    private void initPool() {
        servicePool = new ThreadPoolExecutor(
                2,
                5,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                new ThreadFactoryBuilder().setNameFormat(SERVICE_POOL_PATTERN).build(),
                new ThreadPoolExecutor.AbortPolicy()
        );
        servicePool.prestartCoreThread();
        log.info("{} initialized", SERVICE_POOL_PATTERN);

    }
}
