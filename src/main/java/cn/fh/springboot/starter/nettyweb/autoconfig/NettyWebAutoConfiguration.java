package cn.fh.springboot.starter.nettyweb.autoconfig;

import cn.fh.springboot.starter.nettyweb.network.NettyWebServer;
import cn.fh.springboot.starter.nettyweb.network.handler.NettyWebHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wanghongfei on 2020/1/7.
 */
@Configuration
@EnableConfigurationProperties(NettyWebProp.class)
public class NettyWebAutoConfiguration {
    @Bean
    public NettyWebHandler matchHttpHandler() {
        return new NettyWebHandler();
    }

    @Bean
    @ConditionalOnProperty(prefix = "nettyweb", name = "start-web-server", matchIfMissing = true, havingValue = "true")
    public NettyWebServer matchHttpServer() {
        return new NettyWebServer();
    }

    @Bean
    @ConditionalOnProperty(prefix = "nettyweb", name = "start-web-server", matchIfMissing = true, havingValue = "true")
    public ServiceRegisterBean serviceRegisterBean() {
        return new ServiceRegisterBean();
    }
}
