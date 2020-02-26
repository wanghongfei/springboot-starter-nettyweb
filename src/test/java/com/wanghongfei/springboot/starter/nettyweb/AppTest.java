package com.wanghongfei.springboot.starter.nettyweb;

import com.wanghongfei.springboot.starter.nettyweb.api.DemoRequest;
import com.alibaba.fastjson.JSON;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
    public void testGet() throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet("http://localhost:9090/?name=whf&id=100");
        get.setHeader("Connection", "close");
        CloseableHttpResponse response = client.execute(get);
        String resp = EntityUtils.toString(response.getEntity());

        System.out.println(resp);
        Assert.assertEquals("{\"code\":0,\"data\":\"hello, whf, 100\",\"message\":\"ok\"}", resp);
    }

    @Test
    public void testPost() throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://localhost:9090/post");
        post.setHeader("Connection", "close");

        DemoRequest req = new DemoRequest();
        req.setName("whf");
        String body = JSON.toJSONString(req);

        post.setEntity(new StringEntity(body));

        CloseableHttpResponse response = client.execute(post);
        String resp = EntityUtils.toString(response.getEntity());

        System.out.println(resp);
        Assert.assertEquals("{\"code\":0,\"data\":\"post, whf\",\"message\":\"ok\"}", resp);

    }

    @Test
    public void testNoArg() throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet("http://localhost:9090/raw");
        get.setHeader("Connection", "close");
        CloseableHttpResponse response = client.execute(get);
        String resp = EntityUtils.toString(response.getEntity());

        System.out.println(resp);
        Assert.assertEquals("{\"code\":0,\"data\":\"raw\",\"message\":\"ok\"}", resp);

    }
}
