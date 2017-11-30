package com.github.tingstad.weather.app;

public class MainWeb {

    public static void main(String[] args) {
        WebServer weather = new WebServer();
        weather.run(args);
        while (true /*!weather.stop*/) {
            try {
                Thread.sleep(3_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
//        weather.stop();
    }

}
