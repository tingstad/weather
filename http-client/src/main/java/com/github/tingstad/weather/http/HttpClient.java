package com.github.tingstad.weather.http;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Supplier;

public class HttpClient implements Supplier<InputStream> {

    private final String url;
    private final int connectTimeout;
    private final int readTimeout;

    public HttpClient(String url) {
        this(url, 10_000, 20_000);
    }

    public HttpClient(String url, int connectTimeoutMs, int readTimeoutMs) {
        this.url = url;
        this.connectTimeout = connectTimeoutMs;
        this.readTimeout = readTimeoutMs;
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

        httpURLConnection.setConnectTimeout(connectTimeout);
        httpURLConnection.setReadTimeout(readTimeout);

        return httpURLConnection.getInputStream();
    }

}
