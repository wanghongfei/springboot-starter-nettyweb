package cn.wanghongfei.springboot.starter.nettyweb.autoconfig;

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
    /**
     * netty boss线程数量
     */
    private Integer bossGroupThreadCount = 2;
    /**
     * netty worker线程数量
     */
    private Integer workGroupThreadCount = 0;

    /**
     * http请求最大字节数
     */
    private Integer httpObjectMaxSize = 1024 * 1024 * 5;
    /**
     * 用于标识登录状态的header名
     */
    private String loginTokenHeaderName = "Login-Token";

    /**
     * 业务线程池CoreSize
     */
    private Integer servicePoolCoreSize = 5;
    /**
     * 业务线程池MaxSize
     */
    private Integer servicePoolMaxSize = 10;
    /**
     * 业务线程池任务队列最大长度
     */
    private Integer servicePoolQueueSize = 50;

    /**
     * 是否启动Server
     */
    private Boolean startWebServer = true;
}
