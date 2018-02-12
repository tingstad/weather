package com.github.tingstad.weather.app;

import com.github.tingstad.weather.domain.Status;
import com.github.tingstad.weather.service.api.RealOsloTimeProvider;
import com.github.tingstad.weather.service.api.TimeProvider;
import com.github.tingstad.weather.sms.SmsService;

public class MainJob {

    final private TimeProvider timeProvider;

    public static void main(String[] args) {
        new MainJob(new RealOsloTimeProvider())
                .work();
    }

    public MainJob(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;

    }

    public void work() {
        Status status = new Composer().create(timeProvider).getStatus();
        String content = status.getText();
        if (shouldSendSms(status)) {
            new SmsService().sendSms(content);
        }
    }

    boolean shouldSendSms(Status status) {
        return status.getPriority() != Status.Priority.LOW;
    }

}
