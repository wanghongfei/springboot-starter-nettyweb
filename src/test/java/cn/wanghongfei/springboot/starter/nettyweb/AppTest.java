package cn.wanghongfei.springboot.starter.nettyweb;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by wanghongfei on 2020/2/25.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AppTest {
    @Test
    public void testServerStart() throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet("http://localhost:9090/?name=whf");
        get.setHeader("Connection", "close");
        CloseableHttpResponse response = client.execute(get);
        String resp = EntityUtils.toString(response.getEntity());

        System.out.println(resp);
        Assert.assertEquals("{\"code\":0,\"data\":\"hello, whf\",\"message\":\"ok\"}", resp);
    }
}
