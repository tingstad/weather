package com.github.tingstad.weather.app;

import com.github.tingstad.weather.service.api.RealOsloTimeProvider;

public class MainWeb {

    public static void main(String[] args) {
        WebServer webServer = new WebServer(new Composer().create(new RealOsloTimeProvider()));
        webServer.run(args);
        while (true) {
            try {
                Thread.sleep(9_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
