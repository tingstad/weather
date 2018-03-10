package com.github.tingstad.weather.domain;

import com.github.tingstad.weather.service.api.Status;

public class StatusAll {

    private final String text;
    private final Status.Severity severity;

    public StatusAll(String text, Status.Severity priority) {
        this.text = text;
        this.severity = priority;
    }

    public String getText() {
        return text;
    }

    public Status.Severity getSeverity() {
        return severity;
    }

}
