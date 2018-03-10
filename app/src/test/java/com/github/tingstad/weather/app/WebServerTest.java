package com.github.tingstad.weather.app;

import com.github.tingstad.weather.domain.StatusAll;
import com.github.tingstad.weather.domain.WeatherInterface;
import com.github.tingstad.weather.service.api.Status;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WebServerTest {

    private final static int READ_TIMEOUT = 1_000;
    private final static int CONNECT_TIMEOUT = 1_000;
    private WebServer webServer;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        Optional.ofNullable(webServer)
                .ifPresent(WebServer::stop);
    }

    @Test
    public void testPort8080() throws Exception {
        WeatherInterface weather = () -> new StatusAll("alt ok", Status.Severity.LOW);
        webServer = new WebServer(weather);

        webServer.run(new String[]{"8080"});

        HttpURLConnection httpURLConnection = (HttpURLConnection)
                new URL("http://localhost:8080/")
                        .openConnection();

        httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
        httpURLConnection.setReadTimeout(READ_TIMEOUT);
        InputStream inputStream = httpURLConnection.getInputStream();
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        assertThat(httpURLConnection.getResponseCode(), is(200));
        assertThat(scanner.next(), is(
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>title</title>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <p>alt ok</p>\n" +
                "    <p><a href=\"http://www.yr.no/sted/Norge/Oslo/Oslo/Oslo/\">Værvarsel fra Yr levert av Meteorologisk institutt og NRK</a>\n" +
                "  </body>\n" +
                "</html>"));
    }

    @Test
    public void testPortOtherThan8080() throws Exception {
        WeatherInterface weather = () -> new StatusAll("alt ok", Status.Severity.LOW);
        webServer = new WebServer(weather);

        final String port = "36856";
        webServer.run(new String[]{port});

        HttpURLConnection httpURLConnection = (HttpURLConnection)
                new URL("http://localhost:" + port + "/")
                        .openConnection();

        httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
        httpURLConnection.setReadTimeout(READ_TIMEOUT);
        assertThat(httpURLConnection.getResponseCode(), is(200));
    }

    @Test
    public void exceptionShouldGive500() throws Exception {

        WeatherInterface weather = () -> { throw new RuntimeException(); };
        new WebServer(weather).run(new String[]{"8085"});

        HttpURLConnection httpURLConnection = (HttpURLConnection)
                new URL("http://localhost:8085/")
                        .openConnection();

        httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
        httpURLConnection.setReadTimeout(READ_TIMEOUT);
        assertThat(httpURLConnection.getResponseCode(), is(500));
    }
}
