package cn.fh.springboot.starter.nettyweb.autoconfig;

import cn.fh.springboot.starter.nettyweb.annotation.HttpApi;
import cn.fh.springboot.starter.nettyweb.error.WebException;
import cn.fh.springboot.starter.nettyweb.network.NettyWebServer;
import cn.fh.springboot.starter.nettyweb.network.RequestHandler;
import cn.fh.springboot.starter.nettyweb.network.WebRouter;
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
public class ServiceRegisterBean {
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
            // handler.registerService(apiAnn.path(), apiAnn.paramType(), (RequestHandler) beanObj);
            webRouter.registerService(HttpMethod.valueOf(apiAnn.method()), apiAnn.path(), apiAnn.paramType(), (RequestHandler) beanObj);
        });

        log.info("done scanning @HttpApi");

        // 启动netty
        nettyWebServer.start();
    }
}
