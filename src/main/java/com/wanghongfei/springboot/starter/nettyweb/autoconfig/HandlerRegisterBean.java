package com.wanghongfei.springboot.starter.nettyweb.autoconfig;

import com.wanghongfei.springboot.starter.nettyweb.annotation.HttpApi;
import com.wanghongfei.springboot.starter.nettyweb.error.WebException;
import com.wanghongfei.springboot.starter.nettyweb.network.NettyWebServer;
import com.wanghongfei.springboot.starter.nettyweb.network.RawRequestHandler;
import com.wanghongfei.springboot.starter.nettyweb.network.RequestHandler;
import com.wanghongfei.springboot.starter.nettyweb.network.WebRouter;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by wanghongfei on 2019/11/4.
 */
@Slf4j
public class HandlerRegisterBean {
    @Autowired
    private ApplicationContext springContext;

    @Autowired
    private NettyWebServer nettyWebServer;

    @Autowired
    private WebRouter webRouter;

    @PostConstruct
    private void register() {
        log.info("scanning @HttpApi");

        // 扫描API, 注册到路由中
        Map<String, Object> apiBeanMap = springContext.getBeansWithAnnotation(HttpApi.class);
        apiBeanMap.forEach((beanName, beanObj) -> {
            if (!(beanObj instanceof RequestHandler)) {
                throw new WebException("api bean " + beanObj.getClass() + " is not of type RequestHandler");
            }

            HttpApi apiAnn = beanObj.getClass().getAnnotation(HttpApi.class);
            // 验证注解跟实现的接口是否匹配
            if (!isHandlerWithParam(apiAnn, beanObj) && !isHandlerWithoutParam(apiAnn, beanObj)) {
                throw new IllegalStateException("handler " + beanName + " 's interface does not match its @HttpApi annotation");
            }

            webRouter.registerService(HttpMethod.valueOf(apiAnn.method().toUpperCase()), apiAnn.path(), apiAnn.paramType(), (RequestHandler) beanObj);
        });

        log.info("done scanning @HttpApi");

        // 启动netty
        nettyWebServer.start();
    }

    private boolean isHandlerWithParam(HttpApi an, Object bean) {
        return an.paramType() != Void.class && !(bean instanceof RawRequestHandler);
    }

    private boolean isHandlerWithoutParam(HttpApi an, Object bean) {
        return an.paramType() == Void.class && (bean instanceof RawRequestHandler);
    }
}
