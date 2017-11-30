package com.github.tingstad.weather.app;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;

import static java.lang.Integer.parseInt;

public class WebServer {

    private final static int HTTP_OK = 200;
    private final HttpServer httpServer;
    private boolean stop;

    public WebServer() {
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
        httpServer.createContext("/", httpExchange -> {
            try (
                    OutputStream responseBody = httpExchange.getResponseBody();
                    OutputStreamWriter writer = new OutputStreamWriter(
                            responseBody)
            ) {
                String data = new Weather().getContent();
                writer.append(data);
                httpExchange.sendResponseHeaders(HTTP_OK, data.length());
            } catch (IOException e) {
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

    private static HttpServer createHttpServer() {
        try {
            return HttpServer.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

