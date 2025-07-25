package edu.whpu.client;

import com.alipay.common.tracer.core.utils.StringUtils;
import com.alipay.sofa.tracer.plugins.httpclient.SofaTracerHttpClientBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.TimeUnit;

public class HttpClientInstance {

    /***
     * request timeout
     */
    private static final int    REQUEST_TIMEOUT = 10 * 1000;

    private RequestConfig defaultRequestConfig;

    private CloseableHttpClient httpClient      = null;

    public HttpClientInstance(int soTimeoutMilliseconds) {
        if (soTimeoutMilliseconds < 0) {
            soTimeoutMilliseconds = REQUEST_TIMEOUT;
        }
        //request timeout milliseconds
        int connectionTimeout = 3 * 1000;
        //connect Manager get the Connection timeout milliseconds
        int connectionRequestTimeout = 3 * 1000;
        this.defaultRequestConfig = RequestConfig.custom().setConnectTimeout(connectionTimeout)
                .setSocketTimeout(soTimeoutMilliseconds)
                .setConnectionRequestTimeout(connectionRequestTimeout).build();
        this.httpClient = this.getHttpClient();
    }

    public String executeGet(String url) throws Exception {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }
        HttpGet httpget = new HttpGet(url);
        httpget.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpget.setConfig(defaultRequestConfig);
        return this.execute(httpget);
    }

    private String execute(HttpRequestBase requestBase) throws Exception {
        CloseableHttpResponse response = this.httpClient.execute(requestBase);
        int responseCode = response.getStatusLine().getStatusCode();
        if (HttpStatus.SC_OK != responseCode) {
            return null;
        }
        //https://memorynotfound.com/apache-httpclient-http-get-request-method-example/
        HttpEntity httpEntity = response.getEntity();
        return httpEntity != null ? EntityUtils.toString(httpEntity) : null;
    }

    private CloseableHttpClient getHttpClient() {
        if (this.httpClient != null) {
            return this.httpClient;
        }
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setDefaultMaxPerRoute(6);
        connManager.setMaxTotal(20);
        connManager.closeIdleConnections(120, TimeUnit.SECONDS);
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        //SOFATracer
        SofaTracerHttpClientBuilder.clientBuilder(httpClientBuilder);
        httpClient = httpClientBuilder.setConnectionManager(connManager).disableAutomaticRetries()
                .setUserAgent("CLIENT_VERSION").build();
        return httpClient;
    }
}