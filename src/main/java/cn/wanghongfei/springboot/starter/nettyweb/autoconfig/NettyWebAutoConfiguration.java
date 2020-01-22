package cn.wanghongfei.springboot.starter.nettyweb.autoconfig;

import cn.wanghongfei.springboot.starter.nettyweb.network.NettyWebServer;
import cn.wanghongfei.springboot.starter.nettyweb.network.WebRouter;
import cn.wanghongfei.springboot.starter.nettyweb.network.handler.NettyResponseBuilder;
import cn.wanghongfei.springboot.starter.nettyweb.network.handler.NettyWebExceptionHandler;
import cn.wanghongfei.springboot.starter.nettyweb.network.handler.NettyWebHandler;
import cn.wanghongfei.springboot.starter.nettyweb.validation.ValidatorMapping;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wanghongfei on 2020/1/7.
 */
@Configuration
@EnableConfigurationProperties(NettyWebProp.class)
@ConditionalOnProperty(prefix = "nettyweb", name = "start-web-server", matchIfMissing = true, havingValue = "true")
public class NettyWebAutoConfiguration {
    @Bean
    public NettyWebHandler matchHttpHandler() {
        return new NettyWebHandler();
    }

    @Bean
    public NettyWebServer nettyWebServer() {
        return new NettyWebServer();
    }

    @Bean
    public HandlerRegisterBean serviceRegisterBean() {
        return new HandlerRegisterBean();
    }

    @Bean
    public WebRouter webRouter() {
        return new WebRouter();
    }

    @Bean
    @ConditionalOnMissingBean
    public NettyWebExceptionHandler nettyWebExceptionHandler() {
        return new NettyWebExceptionHandler();
    }

    @Bean
    public ValidatorMapping validatorMapping() {
        return new ValidatorMapping();
    }

    @Bean
    @ConditionalOnMissingBean
    public NettyResponseBuilder nettyResponseBuilder() {
        return new NettyResponseBuilder();
    }
}
