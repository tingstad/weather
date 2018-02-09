package com.github.tingstad.weather.app;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.Integer.parseInt;

public class WebServer {

    private final static int HTTP_OK = 200;
    private final static int HTTP_ERROR = 500;
    private final HttpServer httpServer;
    private final WeatherInterface weather;
    private boolean stop;

    public WebServer(WeatherInterface weather) {
        this.weather = weather;
        httpServer = createHttpServer();
    }

    private static HttpServer createHttpServer() {
        try {
            return HttpServer.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void run(String[] args) {
        int port = parseInt(args[0]);
        System.out.println("Listening on " + port);
        run(port);
    }

    public void stop() {
        stop = true;
        httpServer.stop(0);
    }

    public void run(int port) {
        httpServer.createContext("/", httpExchange -> {

            final Optional<String> content = getContent()
                    .map(toHtml());
            final int responseCode = content.map(c -> HTTP_OK).orElse(HTTP_ERROR);
            final int responseLength = getResponseLength(content);
            httpExchange.sendResponseHeaders(responseCode, responseLength);
            try (
                    OutputStream responseBody = httpExchange.getResponseBody();
                    OutputStreamWriter writer = new OutputStreamWriter(
                            responseBody)
            ) {
                if (content.isPresent()) {
                    writer.append(content.get());
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        });
        try {
            httpServer.bind(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        httpServer.start();
    }

    private int getResponseLength(Optional<String> content) {
        return content.map(c -> {
            try {
                return c.getBytes("UTF-8").length;
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }).orElse(0);
    }

    private Function<String, String> toHtml() {
        return content ->
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>title</title>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <p>" + content + "</p>\n" +
                "    <p><a href=\"http://www.yr.no/sted/Norge/Oslo/Oslo/Oslo/\">Værvarsel fra Yr levert av Meteorologisk institutt og NRK</a>\n" +
                "  </body>\n" +
                "</html>";
    }

    private Optional<String> getContent() {
        try {
            return Optional.of(weather.getContent());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}

