package com.github.tingstad.weather.app;

public class MainWeb {

    public static void main(String[] args) {
        WebServer webServer = new WebServer(Composer.create());
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
