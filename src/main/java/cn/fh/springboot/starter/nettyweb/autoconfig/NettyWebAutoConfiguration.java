package cn.fh.springboot.starter.nettyweb.autoconfig;

import cn.fh.springboot.starter.nettyweb.network.NettyWebServer;
import cn.fh.springboot.starter.nettyweb.network.handler.NettyWebHandler;
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
    public NettyWebServer matchHttpServer() {
        return new NettyWebServer();
    }

    @Bean
    public ServiceRegisterBean serviceRegisterBean() {
        return new ServiceRegisterBean();
    }
}
