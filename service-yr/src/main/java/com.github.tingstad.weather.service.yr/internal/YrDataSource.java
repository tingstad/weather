package com.github.tingstad.weather.service.yr.internal;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Supplier;

public class YrDataSource implements Supplier<InputStream> {

    private final static String URL = "http://www.yr.no/sted/Norge/Oslo/Oslo/Oslo/varsel.xml";

    @Override
    public InputStream get() {
        try {
            return getFromUrl();
        } catch (Exception e) {
            throw new RuntimeException(URL, e);
        }
    }

    private InputStream getFromUrl() throws Exception {
        // http://om.yr.no/verdata/xml/spesifikasjon/

        HttpURLConnection httpURLConnection = (HttpURLConnection)
                new URL(URL)
                        .openConnection();

        httpURLConnection.setConnectTimeout(10_000);
        httpURLConnection.setReadTimeout(20_000);

        return httpURLConnection.getInputStream();
    }

}
