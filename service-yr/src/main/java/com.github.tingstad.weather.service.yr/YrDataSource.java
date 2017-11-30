package com.github.tingstad.weather.service.yr;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class YrDataSource implements DataSource {

    @Override
    public InputStream getData() {
        try {
            return getFromUrl();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private InputStream getFromUrl() throws Exception {
        // http://om.yr.no/verdata/xml/spesifikasjon/

        HttpURLConnection httpURLConnection = (HttpURLConnection)
                new URL("http://www.yr.no/sted/Norge/Oslo/Oslo/Oslo/varsel.xml")
                        .openConnection();

        httpURLConnection.setConnectTimeout(1_000);
        httpURLConnection.setReadTimeout(1_000);

        return httpURLConnection.getInputStream();
    }

}
