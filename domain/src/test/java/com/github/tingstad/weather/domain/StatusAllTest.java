package com.github.tingstad.weather.domain;

import com.github.tingstad.weather.service.api.Status;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StatusAllTest {

    @Test
    public void testToString() {
        assertEquals("Status{ text: foo, severity: LOW, shouldSendSms: false }",
                new StatusAll("foo", Status.Severity.LOW, false)
        .toString());
    }

}
