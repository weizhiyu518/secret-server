package com.secret.uitil;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
public class HttpClientUtil {

    private static HttpClientConnectionManager connMgr;
    private static final int DEFAULT_CONNECT_TIMEOUT = 2 * 1000;
    private static final int DEFAULT_SOCKECT_TIMEOUT = 2 * 1000;


    public static String post(String url, Map<String, Object> paramMap) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            if (paramMap != null) {
                nameValuePairs.addAll(paramMap.keySet().stream().map(paramName -> new BasicNameValuePair(paramName, paramMap.get(paramName).toString())).collect(Collectors.toList()));
            }
            RequestConfig.Builder builder = RequestConfig.custom();
            RequestConfig requestConfig = builder.setSocketTimeout(DEFAULT_SOCKECT_TIMEOUT).setConnectTimeout(DEFAULT_CONNECT_TIMEOUT).build();
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            httpClient = HttpClients.createDefault();
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                log.error("[HttpClientUtil#doPost] 请求失败, url={}, params={}", url, JSONObject.toJSONString(paramMap));
                throw new RuntimeException("请求失败. httpStatusCode = " + statusCode);
            }
            entity = response.getEntity();
            if (entity.getContentType() == null) {
                return EntityUtils.toString(entity, "UTF-8");
            }
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            log.error("[HttpClientUtil#doPost] 发送post请求失败, url={}, params={}", url, paramMap, e);
            throw new RuntimeException(e);
        } finally {
            closeGracefully(httpClient, response, entity);
        }
    }

    public static String postJson(String url, String json) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            StringEntity postingString = new StringEntity(json);
            httpPost.setEntity(postingString);
            httpPost.setHeader("Content-type", "application/json");
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(DEFAULT_SOCKECT_TIMEOUT).setConnectTimeout(DEFAULT_CONNECT_TIMEOUT).build();
            httpPost.setConfig(requestConfig);
            httpClient = HttpClients.createDefault();
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                log.error("[HttpClientUtil#doPost] 请求失败, url={}, json={}", url, json);
                throw new RuntimeException("请求失败. httpStatusCode = " + statusCode);
            }
            entity = response.getEntity();
            if (entity.getContentType() == null) {
                return EntityUtils.toString(entity, "UTF-8");
            }
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            log.error("[HttpClientUtil#doPost] 发送post请求失败, url={}, json={}", url, json, e);
            throw new RuntimeException(e);
        } finally {
            closeGracefully(httpClient, response, entity);
        }
    }


    private static void closeGracefully(CloseableHttpClient httpClient, CloseableHttpResponse response, HttpEntity entity) {
        try {
            EntityUtils.consume(entity);
            if (httpClient != null) {
                httpClient.close();
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            log.error("[HttpClientUtil#doPost] 关闭http post请求资源失败!", e);
        }
    }


}
