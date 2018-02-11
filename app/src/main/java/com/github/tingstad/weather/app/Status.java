package com.github.tingstad.weather.app;

public class Status {

    private final String text;
    private final boolean critical;

    public Status(String text, boolean critical) {
        this.text = text;
        this.critical = critical;
    }

    public String getText() {
        return text;
    }

    public boolean isCritical() {
        return critical;
    }

}
