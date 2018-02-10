package com.github.tingstad.weather.service.ruter;

import com.github.tingstad.weather.service.api.TimeProvider;
import org.junit.Test;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ServiceRuterTest {

    @Test
    public void situationShouldReturnString() {
        TimeProvider timeProvider = () -> LocalDate.of(2018, 2, 10).atStartOfDay();

        String text = new ServiceRuter(new FileDataSource(), timeProvider, 1).getText();

        assertThat(text, is("Forsinkelser"
                + "\nDet er for øyeblikket forsinkelser på enkelte avganger."
                + "\nSjekk sanntid for oppdaterte avgangstider."));
    }

    @Test
    public void situationOfEndedPeriodShouldReturnEmptyString() {
        TimeProvider timeProvider = () -> LocalDateTime.now().plusDays(1);

        String text = new ServiceRuter(new FileDataSource(), timeProvider, 1).getText();

        assertThat(text, is(""));
    }

    @Test
    public void situationStartingAfterCurrentDateShouldReturnEmptyString() {
        TimeProvider timeProvider = () -> LocalDate.of(2018, 1, 1).atStartOfDay();

        String text = new ServiceRuter(new FileDataSource(), timeProvider, 1).getText();

        assertThat(text, is(""));
    }

    @Test
    public void situationWithoutDetail() {
        TimeProvider timeProvider = () -> LocalDate.of(2018, 2, 10).atStartOfDay();

        String text = new ServiceRuter(new FileDataSource(), timeProvider, 3352).getText();

        assertTrue(text.startsWith("Forhåndsbestilling av transport fra enkelte holdeplasser\nSkal du reise fra"));
        assertThat(text.replaceAll("[^\n]", "").length(), is(2));
    }

}

class FileDataSource implements Supplier<InputStream> {

    @Override
    public InputStream get() {
        try {
            return
                    Files.newInputStream(
                            Paths.get(
                                    ServiceRuterTest.class.getResource("/data.xml").toURI()
                            )
                    );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}