package com.github.tingstad.weather.sms;

import org.junit.Ignore;
import org.junit.Test;

public class SmsServiceIntegrationTest {

    @Ignore
    @Test
    public void test() {
        new SmsService("http://localhost:8080").sendSms("test");
    }

}
