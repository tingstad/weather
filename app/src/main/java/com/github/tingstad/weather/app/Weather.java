package com.github.tingstad.weather.app;

import com.github.tingstad.weather.service.Service;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;

import static java.lang.Integer.parseInt;

public class Weather {

    private final static int HTTP_OK = 200;
    private final HttpServer httpServer;
    private boolean stop;

    public Weather() {
        httpServer = createHttpServer();
    }

    void run(String[] args) {
        System.out.println("Hello");
        run(parseInt(args[0]));
    }

    public void stop() {
        stop = true;
        httpServer.stop(0);
    }

    public void run(int port) {
        httpServer.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                try (
                        OutputStream responseBody = exchange.getResponseBody();
                        OutputStreamWriter writer = new OutputStreamWriter(
                                responseBody)
                ) {
                    String data = "Hei";
                    writer.append(data);
                    exchange.sendResponseHeaders(HTTP_OK, data.length());
                } catch (IOException e) {
                    throw e;
                }
            }
        });
        try {
            httpServer.bind(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        httpServer.start();
    }

    private static HttpServer createHttpServer() {
        try {
            return HttpServer.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
