package me.lv.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpUtils {
    private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);
    private static final String DEFAULT_CHARSET = "utf-8";
    private static final int DEFALUT_CONNECT_TIMEOUT = 5000;
    private static final int DEFAULT_SOCKET_TIMEOUT = 30000;

    public HttpUtils() {
    }

    public static String doGet(String url, Map<String, String> params) throws Exception {
        return doGet(url, params, (Map)null, "utf-8", 5000, 30000);
    }

    public static String doGet(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        return doGet(url, params, headers, "utf-8", 5000, 30000);
    }

    public static String doGet(String url, Map<String, String> params, String charset) throws Exception {
        return doGet(url, params, (Map)null, charset, 5000, 30000);
    }

    public static String doPost(String url, Map<String, String> params) throws Exception {
        return doPost(url, (Map)params, (Map)null, "utf-8", 5000, 30000);
    }

    public static String doPost(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        return doPost(url, (Map)params, headers, "utf-8", 5000, 30000);
    }

    public static String doPost(String url, Map<String, String> params, String charset) throws Exception {
        return doPost(url, (Map)params, (Map)null, charset, 5000, 30000);
    }

    public static String doGet(String url, Map<String, String> params, Map<String, String> headers, String charset, int connectTimeout, int socketTimeout) throws Exception {
        CloseableHttpClient httpClient = createClient(url);

        String var11;
        try {
            HttpGet httpGet = new HttpGet(buildGetUrl(url, params, charset));
            RequestConfig config = RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
            httpGet.setConfig(config);
            addHeaders(httpGet, headers);
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new ClientProtocolException("status code=" + response.getStatusLine().getStatusCode());
            }

            String result = read(response, charset);
            if (LOG.isDebugEnabled()) {
                LOG.debug("response={}", result);
            }

            var11 = result;
        } finally {
            try {
                httpClient.close();
            } catch (IOException var18) {
                ;
            }

        }

        return var11;
    }

    public static String doPost(String url, Map<String, String> params, Map<String, String> headers, String charset, int connectTimeout, int socketTimeout) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String var11;
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
            buildEntity(httpPost, params, charset);
            httpPost.setConfig(config);
            addHeaders(httpPost, headers);
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new ClientProtocolException("status code=" + response.getStatusLine().getStatusCode());
            }

            String result = read(response, charset);
            if (LOG.isDebugEnabled()) {
                LOG.debug("response={}", result);
            }

            var11 = result;
        } finally {
            try {
                httpClient.close();
            } catch (IOException var18) {
                ;
            }

        }

        return var11;
    }

    public static String doPost(String url, String content, String charset) throws Exception {
        return doPost(url, (String)content, (Map)null, charset, 5000, 30000);
    }

    public static String doPost(String url, String content, Map<String, String> headers, String charset) throws Exception {
        return doPost(url, (String)content, headers, charset, 5000, 30000);
    }

    public static String doPost(String url, String content, Map<String, String> headers, String charset, int connectTimeout, int socketTimeout) throws Exception {
        CloseableHttpClient httpClient = createClient(url);

        String var12;
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
            httpPost.setConfig(config);
            addHeaders(httpPost, headers);
            StringEntity entity = new StringEntity(content, charset);
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new ClientProtocolException("status code=" + response.getStatusLine().getStatusCode());
            }

            String result = read(response, charset);
            if (LOG.isDebugEnabled()) {
                LOG.debug("response={}", result);
            }

            var12 = result;
        } finally {
            try {
                httpClient.close();
            } catch (IOException var19) {
                ;
            }

        }

        return var12;
    }

    private static void addHeaders(HttpRequestBase request, Map<String, String> headers) {
        if (headers != null) {
            Iterator var2 = headers.keySet().iterator();

            while(var2.hasNext()) {
                String key = (String)var2.next();
                request.addHeader(key, (String)headers.get(key));
            }
        }

    }

    private static void buildEntity(HttpPost httpPost, Map<String, String> parameters, String encoding) throws UnsupportedEncodingException {
        List<NameValuePair> params = new ArrayList();
        if (null != parameters && !parameters.isEmpty()) {
            Iterator var4 = parameters.entrySet().iterator();

            while(var4.hasNext()) {
                Entry<String, String> entry = (Entry)var4.next();
                params.add(new BasicNameValuePair((String)entry.getKey(), null != entry.getValue() ? (String)entry.getValue() : ""));
            }
        }

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, encoding);
        httpPost.setEntity(entity);
    }

    private static String buildGetUrl(String url, Map<String, String> parameters, String encoding) throws IOException {
        if (StringUtils.isBlank(url)) {
            throw new IOException("url is empty!");
        } else {
            StringBuilder sb = new StringBuilder();
            if (null != parameters && !parameters.isEmpty()) {
                Iterator var4 = parameters.entrySet().iterator();

                while(var4.hasNext()) {
                    Entry<String, String> entry = (Entry)var4.next();
                    sb.append("&").append((String)entry.getKey()).append("=").append(null != entry.getValue() ? URLEncoder.encode((String)entry.getValue(), encoding) : "");
                }

                sb.substring(1);
            }

            String queryString = sb.toString().trim();
            url = url.trim();
            if (!StringUtils.isBlank(queryString)) {
                if (url.endsWith("?")) {
                    url = url + queryString;
                } else {
                    url = url + "?" + queryString;
                }
            }

            return url;
        }
    }

    private static String read(HttpResponse response, String encoding) throws IOException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        Charset charset = ContentType.getOrDefault(response.getEntity()).getCharset();
        if (charset == null) {
            charset = Charset.forName(encoding);
        }

        InputStream is = response.getEntity().getContent();
        InputStreamReader inputStreamReader = new InputStreamReader(is, charset);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        String s;
        while((s = reader.readLine()) != null) {
            sb.append(s);
        }

        reader.close();
        return sb.toString();
    }

    private static CloseableHttpClient createClient(String url) throws KeyManagementException, NoSuchAlgorithmException {
        return url.startsWith("https://") ? sslClient() : plainClient();
    }

    private static CloseableHttpClient plainClient() throws NoSuchAlgorithmException, KeyManagementException {
        RequestConfig requestConfig = RequestConfig.custom().setCookieSpec("standard-strict").setExpectContinueEnabled(Boolean.TRUE).setTargetPreferredAuthSchemes(Arrays.asList("NTLM", "Digest")).setProxyPreferredAuthSchemes(Arrays.asList("Basic")).build();
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
        Registry<ConnectionSocketFactory> socketFactoryRegistry = registryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE).build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        CloseableHttpClient closeableHttpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).build();
        return closeableHttpClient;
    }

    private static CloseableHttpClient sslClient() throws NoSuchAlgorithmException, KeyManagementException {
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init((KeyManager[])null, new TrustManager[]{trustManager}, (SecureRandom)null);
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        RequestConfig requestConfig = RequestConfig.custom().setCookieSpec("standard-strict").setExpectContinueEnabled(Boolean.TRUE).setTargetPreferredAuthSchemes(Arrays.asList("NTLM", "Digest")).setProxyPreferredAuthSchemes(Arrays.asList("Basic")).build();
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
        Registry<ConnectionSocketFactory> socketFactoryRegistry = registryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslConnectionSocketFactory).build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        CloseableHttpClient closeableHttpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).build();
        return closeableHttpClient;
    }
}
