package com.github.tingstad.weather.service.api;

public interface Status {

    enum Severity {
        LOW,
        MEDIUM,
        HIGH
    }

    String getText();
    Severity getSeverity();

    static Status create(String text, Status.Severity severity) {
        return new Status() {
            @Override
            public String getText() {
                return text;
            }

            @Override
            public Severity getSeverity() {
                return severity;
            }
        };
    }

}

