package com.wanghongfei.springboot.starter.nettyweb.api;

import com.alibaba.fastjson.JSON;
import com.wanghongfei.springboot.starter.nettyweb.api.component.DemoRequest;
import com.wanghongfei.springboot.starter.nettyweb.api.component.DemoValidationRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * Created by wanghongfei on 2020/2/25.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ApiTest {
    @Test
    public void testGet() throws Exception {
        HttpGet get = new HttpGet("http://localhost:9090/?name=whf&id=100");
        String resp = sendRequest(get);

        System.out.println(resp);
        Assert.assertEquals("{\"code\":0,\"data\":\"hello, whf, 100\",\"message\":\"ok\"}", resp);
    }

    @Test
    public void testPost() throws Exception {
        HttpPost post = new HttpPost("http://localhost:9090/post");

        DemoRequest req = new DemoRequest();
        req.setName("whf");
        String body = JSON.toJSONString(req);

        post.setEntity(new StringEntity(body));

        String resp = sendRequest(post);

        System.out.println(resp);
        Assert.assertEquals("{\"code\":0,\"data\":\"post, whf\",\"message\":\"ok\"}", resp);

    }

    @Test
    public void testNoArg() throws Exception {
        HttpGet get = new HttpGet("http://localhost:9090/raw");
        String resp = sendRequest(get);

        System.out.println(resp);
        Assert.assertEquals("{\"code\":0,\"data\":\"raw\",\"message\":\"ok\"}", resp);

    }

    @Test
    public void testStringCandidateValidation() throws Exception {
        DemoValidationRequest request = new DemoValidationRequest();
        request.setName("not a good name");
        String body = JSON.toJSONString(request);

        HttpPost post = new HttpPost("http://localhost:9090/validation");
        post.setEntity(new StringEntity(body));

        String resp = sendRequest(post);
        System.out.println(resp);

        Assert.assertEquals("{\"code\":400,\"message\":\"invalid name\"}", resp);
    }

    @Test
    public void testNumberValidation() throws Exception {
        DemoValidationRequest request = new DemoValidationRequest();
        request.setName("bruce");
        request.setAge(-1L);
        String body = JSON.toJSONString(request);

        HttpPost post = new HttpPost("http://localhost:9090/validation");
        post.setEntity(new StringEntity(body));

        String resp = sendRequest(post);
        System.out.println(resp);

        Assert.assertEquals("{\"code\":400,\"message\":\"invalid age\"}", resp);
    }

    public String sendRequest(HttpUriRequest request) throws IOException {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        request.setHeader("Connection", "close");
        try {
            client = HttpClients.createDefault();
            response = client.execute(request);
            return EntityUtils.toString(response.getEntity());

        } catch (IOException e) {
            throw e;

        } finally {
            if (null != client) {
                client.close();
            }
            if (null != response) {
                response.close();
            }
        }
    }
}
