package cn.wanghongfei.springboot.starter.nettyweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created by wanghongfei on 2020/2/25.
 */
@SpringBootApplication
public class TestApplication {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(TestApplication.class, args);
    }
}
