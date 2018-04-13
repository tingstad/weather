package com.github.tingstad.weather.service.api;

import com.github.tingstad.weather.service.api.Status.Severity;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class StatusTest {

    @Test
    public void testSeverityComparison() {
        List<Severity> sorted = Stream.of(Severity.MEDIUM, Severity.HIGH, Severity.LOW)
                .sorted()
                .collect(toList());

        assertEquals(
                asList(Severity.LOW, Severity.MEDIUM, Severity.HIGH)
                , sorted
        );
    }

}
