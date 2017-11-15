package com.github.tingstad.weather.app;

public class Main {

    public static void main(String[] args) {
        Weather weather = new Weather();
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
