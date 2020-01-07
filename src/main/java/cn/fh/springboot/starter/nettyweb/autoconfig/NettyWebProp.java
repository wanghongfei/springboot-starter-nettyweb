package cn.fh.springboot.starter.nettyweb.autoconfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by wanghongfei on 2020/1/7.
 */
@ConfigurationProperties(prefix = "nettyweb")
@Component
@Getter
@Setter
@ToString
public class NettyWebProp {
    private Integer bossGroupThreadCount = 2;
    private Integer workGroupThreadCount = 0;

    private Integer httpObjectMaxSize = 1024 * 1024 * 5;
    private String loginTokenHeaderName = "Login-Token";

    private Integer servicePoolCoreSize = 5;
    private Integer servicePoolMaxSize = 10;
    private Integer servicePoolQueueSize = 50;
}
