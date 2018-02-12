package com.github.tingstad.weather.app;

import com.github.tingstad.weather.domain.Status;
import com.github.tingstad.weather.sms.SmsService;

public class MainJob {

    public static void main(String[] args) {
        new MainJob().work();
    }

    public void work() {
        Status status = new Composer().create().getStatus();
        String content = status.getText();
        if (shouldSendSms(status)) {
            new SmsService().sendSms(content);
        }
    }

    boolean shouldSendSms(Status status) {
        return status.getPriority() != Status.Priority.LOW;
    }

}
