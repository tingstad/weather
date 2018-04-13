package com.github.tingstad.weather.domain;

import com.github.tingstad.weather.service.api.Status;

public class StatusAll {

    private final String text;
    private final Status.Severity severity;
    private final boolean shouldSendSms;

    public StatusAll(String text, Status.Severity priority, boolean shouldSendSms) {
        this.text = text;
        this.severity = priority;
        this.shouldSendSms = shouldSendSms;
    }

    public String getText() {
        return text;
    }

    public Status.Severity getSeverity() {
        return severity;
    }

    public boolean shouldSendSms() {
        return shouldSendSms;
    }

    @Override
    public String toString() {
        return String.format("Status{ text: %s, severity: %s, shouldSendSms: %s }",
                text, severity, shouldSendSms);
    }

}
