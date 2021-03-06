package com.wanghongfei.springboot.starter.nettyweb.network.handler;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wanghongfei.springboot.starter.nettyweb.annotation.validation.Validation;
import com.wanghongfei.springboot.starter.nettyweb.autoconfig.NettyWebProp;
import com.wanghongfei.springboot.starter.nettyweb.error.WebException;
import com.wanghongfei.springboot.starter.nettyweb.network.WebRouter;
import com.wanghongfei.springboot.starter.nettyweb.network.inject.InjectHeaders;
import com.wanghongfei.springboot.starter.nettyweb.network.inject.InjectLoginToken;
import com.wanghongfei.springboot.starter.nettyweb.network.inject.InjectRequestId;
import com.wanghongfei.springboot.starter.nettyweb.network.vo.CommonResponse;
import com.wanghongfei.springboot.starter.nettyweb.utils.SnowFlake;
import com.wanghongfei.springboot.starter.nettyweb.validation.ValidatorMapping;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private WebRouter router;

    @Autowired
    private NettyWebExceptionHandler exceptionHandler;

    @Autowired
    private ValidatorMapping validatorMapping;

    @Autowired
    private NettyResponseBuilder responseBuilder;

    private ThreadPoolExecutor servicePool;

    private AttributeKey<String> uriKey = AttributeKey.valueOf("uriKey");
    private AttributeKey<Long> uidKey = AttributeKey.valueOf("uidKey");



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        HttpMethod method = httpRequest.method();

        // 取出path
        String path = httpRequest.uri();
        ctx.channel().attr(uriKey).set(path);

        // 取出service
        RequestHandler handler = router.matchHandler(method, path);
        if (null == handler) {
            throw new WebException("404");
        }

        // 取出参数对象类型
        Class<?> paramType = router.matchArgType(method, path);
        if (null == paramType) {
            throw new WebException("lack argument for this path");
        }

        // 取出请求体
        String body = extractRequestBody(httpRequest);

        // 生成唯一标识
        Long uid = SnowFlake.genId();
        ctx.channel().attr(uidKey).set(uid);

        // 打印请求日志
        log.info("{} request for {} {}, body: {}", uid, method, path, body);


        // 参数解析
        // 用MutableObject是为了能在lambda中传递参数对象
        MutableObject<Object> paramObjectContainer = new MutableObject<>();
        // 只有当参数对象类型不是Void时才需要解析参数
        if (Void.class != paramType) {
            // 如果是POST请求, body还为空, 报错
            if (method == HttpMethod.POST && StringUtils.isEmpty(body)) {
                throw new WebException("body is empty");
            }

            // 解析参数
            Object paramObject = deserializeParam(httpRequest, paramType, uid, body);
            paramObjectContainer.setValue(paramObject);
        }


        // 执行参数验证
        if (null != paramObjectContainer.getValue()) {
            validateParam(paramObjectContainer.getValue(), false);
        }

        // 调用service
        servicePool.execute(() -> {
            try {
                Object retObj = null;
                if (handler instanceof RawRequestHandler) {
                    // 是无参数的handler
                    retObj = ((RawRequestHandler) handler).serveRequest();

                } else if (handler instanceof AroundRequestHandler) {
                    // 是带有pre, post处理器的handler
                    AroundRequestHandler aroundHandler = (AroundRequestHandler) handler;

                    // 调用前置方法
                    boolean canContinue = aroundHandler.before(paramObjectContainer.getValue(), ctx);
                    if (!canContinue) {
                        return;
                    }

                    // 调用业务逻辑
                    retObj = aroundHandler.serveRequest(paramObjectContainer.getValue());

                    // 调用后置方法
                    aroundHandler.after(paramObjectContainer.getValue(), retObj);

                } else {
                    // 是普通的handler
                    retObj = handler.serveRequest(paramObjectContainer.getValue());
                }

                FullHttpResponse response =
                        responseBuilder.buildHttpResponse(retObj, CommonResponse.MESSAGE_OK, CommonResponse.CODE_OK, log, path, uid);
                handler.modifyHeader(response.headers(), retObj);

                ctx.writeAndFlush(response);

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

        NettyWebExceptionHandler.CodeAndMessage codeAndMessage = exceptionHandler.handleException(cause);

        String path = ctx.channel().attr(uriKey).get();
        Long uid = ctx.channel().attr(uidKey).get();

        FullHttpResponse response =
                responseBuilder.buildHttpResponse(null, codeAndMessage.getMessage(), codeAndMessage.getCode(), log, path, uid);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public void close() {
        if (null != this.servicePool) {
            this.servicePool.shutdownNow();
        }
    }

    private void validateParam(Object param, boolean validateDirectly) {
        Class<?> type = param.getClass();
        if (!validateDirectly) {
            Validation annValidation = type.getAnnotation(Validation.class);
            if (null == annValidation) {
                // 不需要验证
                return;
            }
        }


        ReflectionUtils.doWithFields(type, field -> {
            ReflectionUtils.makeAccessible(field);

            Annotation[] anns = field.getAnnotations();
            for (Annotation an : anns) {
                Object fieldValue = field.get(param);
                validatorMapping.invokeValidator(an, fieldValue, field.getType());

                // 如果有@Validation注解
                if (an instanceof Validation) {
                    // 递归验证
                    validateParam(fieldValue, true);
                }
            }
        });
    }

    private Object deserializeParam(FullHttpRequest httpRequest, Class<?> paramType, Long uid, String body) {
        Object ret;
        if (httpRequest.method() == HttpMethod.POST) {
            // 将body转成string
            if (StringUtils.isEmpty(body)) {
                throw new WebException("body is empty");
            }

            // 反序列化
            ret = transBodyType(body, paramType);

        } else {
            // 是GET请求
            QueryStringDecoder decoder = new QueryStringDecoder(httpRequest.uri());

            // 构造参数对象
            Object param = BeanUtils.instantiateClass(paramType);

            // 反射赋值
            decoder.parameters().forEach((key, value) -> {
                // value是一个List, 只取第一个元素
                Field field = ReflectionUtils.findField(paramType, key);
                if (null == field) {
                    return;
                }

                Object fieldValue = value.get(0);

                try {
                    if (field.getType() == Integer.class) {
                        fieldValue = Integer.valueOf(value.get(0));

                    } else if (field.getType() == Long.class) {
                        fieldValue = Long.valueOf(value.get(0));

                    } else if (field.getType() == String.class) {
                        // do nothing

                    } else if (field.getType() == BigDecimal.class) {
                        fieldValue = new BigDecimal(value.get(0));

                    } else {
                        throw new IllegalStateException("unsupported param type " + field);
                    }

                } catch (NumberFormatException e) {
                    throw new WebException("invalid arg type");
                }


                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, param, fieldValue);
            });

            ret = param;
        }

        // 注入所需字段
        injectFields(ret, httpRequest, uid);

        return ret;
    }

    private String extractRequestBody(FullHttpRequest request) {
        if (request.content().readableBytes() > 0) {
            return request.content().toString(StandardCharsets.UTF_8);
        }

        return "";
    }

    private void injectFields(Object param, FullHttpRequest httpRequest, Long reqId) {
        // 注入loginToken
        if (param instanceof InjectLoginToken) {
            InjectLoginToken injectLoginToken = (InjectLoginToken) param;

            String loginToken = httpRequest.headers().get(prop.getLoginTokenHeaderName());
            if (StringUtils.isEmpty(loginToken)) {
                throw new WebException("lack token");
            }

            injectLoginToken.setLoginToken(loginToken);
        }

        // 注入header
        if (param instanceof InjectHeaders) {
            InjectHeaders injectHeaders = (InjectHeaders) param;

            List<Map.Entry<String, String>> entries = httpRequest.headers().entries();
            Map<String, String> headerMap = new HashMap<>();
            entries.forEach(kv -> headerMap.put(kv.getKey(), kv.getValue()));

            injectHeaders.setHeaderMap(headerMap);
        }

        if (param instanceof InjectRequestId) {
            InjectRequestId injectRequestId = (InjectRequestId) param;
            injectRequestId.setRequestId(reqId);
        }
    }

    private Object transBodyType(String body, Class paramType) {
        try {
            return JSON.parseObject(body, paramType);

        } catch (Throwable e) {
            log.warn("failed to deserialize request json", e);
            throw new WebException("body does not match");
        }
    }

    @PostConstruct
    private void initPool() {
        servicePool = new ThreadPoolExecutor(
                prop.getServicePoolCoreSize(),
                prop.getServicePoolMaxSize(),
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(prop.getServicePoolQueueSize()),
                new ThreadFactoryBuilder().setNameFormat(SERVICE_POOL_PATTERN).build(),
                new ThreadPoolExecutor.AbortPolicy()
        );
        servicePool.prestartCoreThread();
        log.info("{} initialized", SERVICE_POOL_PATTERN);

    }
}
