package com.github.tingstad.weather;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;

public class Weather {

    private final static int HTTP_OK = 200;

    public static void main(String[] args) {
        System.out.println("Hello");
        new Weather().run(8080);
    }

    public void run(int port) {
        HttpServer httpServer = createHttpServer();
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

        while (true) {
            try {
                Thread.sleep(5_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static HttpServer createHttpServer() {
        try {
            return HttpServer.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
