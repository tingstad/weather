package com.github.tingstad.weather.http;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
       ***     ***
     **   ** **   **
    *       ´       *
    *  immutability *
     *             *
      **         **
        **     **
          ** **
            *
 **/
public class HttpClient implements Supplier<InputStream> {

    private final String url;
    private final int connectTimeout;
    private final int readTimeout;
    private final Map<String, String> headers;

    public HttpClient(String url) {
        this(url, 10_000, 20_000);
    }

    public HttpClient(String url, int connectTimeoutMs, int readTimeoutMs) {
        this.url = url;
        this.connectTimeout = connectTimeoutMs;
        this.readTimeout = readTimeoutMs;
        this.headers = new HashMap<>();
    }

    private HttpClient(HttpClient origin, Map<String, String> headers) {
        this.url = origin.url;
        this.connectTimeout = origin.connectTimeout;
        this.readTimeout = origin.readTimeout;
        this.headers = concat(headers, origin.headers);
    }

    public HttpClient headers(Map<String, String> headers) {
        return new HttpClient(this, headers);
    }

    public HttpClient header(String key, String value) {
        return headers(Collections.singletonMap(key, value));
    }

    @Override
    public InputStream get() {
        try {
            return getFromUrl();
        } catch (Exception e) {
            throw new RuntimeException(url, e);
        }
    }

    private InputStream getFromUrl() throws Exception {
        HttpURLConnection httpURLConnection = (HttpURLConnection)
                new URL(url)
                        .openConnection();

        headers.keySet().stream()
                .forEach(key ->
                        httpURLConnection.setRequestProperty(key, headers.get(key)));
        httpURLConnection.setConnectTimeout(connectTimeout);
        httpURLConnection.setReadTimeout(readTimeout);

        return httpURLConnection.getInputStream();
    }

    private static <K, V> Map<K, V> concat(Map<K, V> map1, Map<K, V> map2) {
        return Map.ofEntries(
                Stream.concat(
                        map1.entrySet().stream(),
                        map2.entrySet().stream()
                ).toArray(Map.Entry[]::new));
    }

}
