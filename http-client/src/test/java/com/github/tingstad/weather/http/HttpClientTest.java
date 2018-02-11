package com.github.tingstad.weather.http;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class HttpClientTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Test(expected = Exception.class)
    public void invalidUrlShouldThrowException() {
        new HttpClient("invalid").get();
    }

    @Test
    public void workingUrlShouldReturnContent() {
        WireMock.stubFor(
                WireMock.get("/path")
                        .willReturn(
                                WireMock.ok("Hello")
                        )
        );

        InputStream inputStream = new HttpClient(wireMockRule.url("/path")).get();

        String content = new Scanner(inputStream).useDelimiter("\\A").next();
        assertThat(content, is("Hello"));
    }

    @Test(expected = RuntimeException.class)
    public void notFoundShouldThrowException() {
        WireMock.stubFor(
                WireMock.get("/path")
                        .willReturn(
                                WireMock.notFound()
                        )
        );

        new HttpClient(wireMockRule.url("/path")).get();
    }

    @Test(expected = RuntimeException.class)
    public void serverErrorShouldThrowException() {
        WireMock.stubFor(
                WireMock.get("/path")
                        .willReturn(
                                WireMock.serverError()
                        )
        );

        new HttpClient(wireMockRule.url("/path")).get();
    }

    @Test
    public void connectionRefusedShouldThrowException() {
        HttpClient httpClient = new HttpClient("http://localhost:" + (wireMockRule.port() + 1) + "/");

        try {
            httpClient.get();
            fail("Exception expected");
        } catch (RuntimeException e) {
            assertEquals(e.getCause().getClass(), ConnectException.class);
        }
    }

    @Test
    public void unknownHostShouldThrowException() {
        HttpClient httpClient = new HttpClient("http://foo.example/");

        try {
            httpClient.get();
            fail("Exception expected");
        } catch (RuntimeException e) {
            assertEquals(e.getCause().getClass(), UnknownHostException.class);
        }
    }

    @Test
    public void connectionResetShouldThrowException() {
        WireMock.stubFor(
                WireMock.get("/path")
                        .willReturn(
                                WireMock.aResponse()
                                        .withFault(Fault.CONNECTION_RESET_BY_PEER)
                        )
        );

        try {
            new HttpClient(wireMockRule.url("/path")).get();
            fail("Exception expected");
        } catch (RuntimeException e) {
            assertEquals(e.getCause().getClass(), SocketException.class);
        }
    }

    @Test
    public void garbageShouldThrowException() {
        WireMock.stubFor(
                WireMock.get("/path")
                        .willReturn(
                                WireMock.aResponse()
                                        .withFault(Fault.RANDOM_DATA_THEN_CLOSE)
                        )
        );

        try {
            new HttpClient(wireMockRule.url("/path")).get();
            fail("Exception expected");
        } catch (RuntimeException e) {
            assertEquals(e.getCause().getClass(), IOException.class);
        }
    }

    @Test
    public void slowResponseShouldTriggerReadTimeout() {
        WireMock.stubFor(
                WireMock.get("/path")
                        .willReturn(
                                WireMock.aResponse()
                                        .withFixedDelay(1_000)
                        )
        );

        try {
            new HttpClient(wireMockRule.url("/path"),
                    2_000,
                    1).get();
            fail("Exception expected");
        } catch (RuntimeException e) {
            assertEquals(e.getCause().getClass(), SocketTimeoutException.class);
        }
    }

    @Test
    public void providedHeaderShouldBeSent() {
        WireMock.stubFor(
                WireMock.get("/path")
                        .withHeader("Foo", WireMock.equalTo("Too"))
                        .willReturn(
                                WireMock.ok()
                        )
        );
        new HttpClient(wireMockRule.url("/path"))
                .header("Foo", "Too")
                .get();
    }

    @Test
    public void providedHeadersShouldBeSent() {
        WireMock.stubFor(
                WireMock.get("/path")
                        .withHeader("Foo", WireMock.equalTo("Too"))
                        .withHeader("Boo", WireMock.equalTo("Coo"))
                        .willReturn(
                                WireMock.ok()
                        )
        );
        new HttpClient(wireMockRule.url("/path"))
                .header("Foo", "Too")
                .header("Boo", "Coo")
                .get();
    }

    @Test
    public void providedHeadersInMapShouldBeSent() {
        WireMock.stubFor(
                WireMock.get("/path")
                        .withHeader("Foo", WireMock.equalTo("Too"))
                        .withHeader("Boo", WireMock.equalTo("Coo"))
                        .willReturn(
                                WireMock.ok()
                        )
        );
        new HttpClient(wireMockRule.url("/path"))
                .headers(Map.of(
                        "Foo", "Too",
                        "Boo", "Coo")
                )
                .get();
    }

}
