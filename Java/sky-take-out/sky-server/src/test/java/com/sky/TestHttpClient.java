package com.sky;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试HttpClient
 */
// @SpringBootTest  // 当前测试类不需要使用spring环境，可以不加该注解
public class TestHttpClient {
    // 需求1：通过HttpClient 发送get请求，请求苍穹外卖用户端的查看店铺营业状态接口。/user/shop/status
    @Test
    public void testGet() throws Exception {
        // 1.创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 2.创建HttpGet请求对象--构造出请求路径、请求参数
        HttpGet httpGet = new HttpGet("http://localhost:8080/user/shop/status");

        // 3.调用HttpClient对象的execute方法，发送请求
        CloseableHttpResponse response = httpClient.execute(httpGet);

        // 4.解析响应数据
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("statusCode = " + statusCode);
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity);
        System.out.println("result = "+result);

        // 5.释放资源
        response.close();
        httpClient.close();
    }


    // 需求2：通过HttpClient 发送post请求，请求苍穹外卖管理端的员工登录接口。/admin/employee/login
    @Test
    public void testPost() throws Exception {
        // 1.创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 2.创建HttpPost请求对象--构造出请求路径、请求参数
        HttpPost httpPost = new HttpPost("http://localhost:8080/admin/employee/login");

        // 2.1构造请求体参数
        Map map = new HashMap();
        map.put("username", "admin");
        map.put("password", "123456");
        StringEntity httpEntity = new StringEntity(JSON.toJSONString(map));
        // 2.2设置请求体的编码以及类型  否则报错："status":415,"error":"Unsupported Media Type"
        httpEntity.setContentEncoding("utf-8");
        httpEntity.setContentType("application/json");
        httpPost.setEntity(httpEntity);

        // 3.调用HttpClient对象的execute方法，发送请求
        CloseableHttpResponse response = httpClient.execute(httpPost);

        // 4.解析响应数据
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("statusCode = " + statusCode);
        String result = EntityUtils.toString(response.getEntity());
        System.out.println("result = "+result);

        // 5.释放资源
        response.close();
        httpClient.close();
    }
}
