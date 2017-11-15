package com.github.tingstad.weather.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WeatherTest {

    private Weather weather;

    @Before
    public void setUp() {
        weather = new Weather();
    }

    @After
    public void tearDown() {
        weather.stop();
    }

    @Test
    public void testPort8080() throws Exception {

        weather.run(new String[]{"8080"});

        HttpURLConnection httpURLConnection = (HttpURLConnection)
                new URL("http://localhost:8080/")
                        .openConnection();

        httpURLConnection.setConnectTimeout(1_000);
        httpURLConnection.setReadTimeout(1_000);
        assertThat(httpURLConnection.getResponseCode(), is(200));
    }

    @Test
    public void testPortOtherThan8080() throws Exception {

        final String port = "36856";
        weather.run(new String[]{port});

        HttpURLConnection httpURLConnection = (HttpURLConnection)
                new URL("http://localhost:" + port + "/")
                        .openConnection();

        httpURLConnection.setConnectTimeout(1_000);
        httpURLConnection.setReadTimeout(1_000);
        assertThat(httpURLConnection.getResponseCode(), is(200));
    }
}
