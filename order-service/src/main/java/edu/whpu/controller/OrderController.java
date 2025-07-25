package edu.whpu.controller;

import com.alipay.sofa.tracer.plugins.httpclient.SofaTracerHttpClientBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/order")
public class OrderController {

    public String test() throws IOException {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setDefaultMaxPerRoute(6);
        connManager.setMaxTotal(20);
        connManager.closeIdleConnections(120, TimeUnit.SECONDS);
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // SOFATracer
        SofaTracerHttpClientBuilder.clientBuilder(httpClientBuilder);
        CloseableHttpClient httpClient = httpClientBuilder.setConnectionManager(connManager).disableAutomaticRetries()
                .build();
        // 调用9090端口的服务
        CloseableHttpResponse response1 = httpClient.execute(new HttpGet("http://localhost:9090/demo"));
        // 调用9091端口的服务
        CloseableHttpResponse response2 = httpClient.execute(new HttpGet("http://localhost:9091/demo"));
        new Thread(() -> {
            try {
                httpClient.execute(new HttpGet("http://localhost:9091/demo"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        return EntityUtils.toString(response1.getEntity());
    }
}
