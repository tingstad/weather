package com.github.tingstad.weather.service.yr;

import com.github.tingstad.weather.service.api.Service;
import org.junit.Test;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Supplier;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ServiceYrTest {

    @Test
    public void test() {

        Service service = new ServiceYr(new FileDataSource());

        String text = service.getText();

        assertThat(text, is("28/11 12-18: Snø, 1mm, Frisk bris, 1 grader"));
    }

}

class FileDataSource implements Supplier<InputStream> {

    @Override
    public InputStream get() {
        try {
            return
                    Files.newInputStream(
                            Paths.get(
                                    ServiceYrTest.class.getResource("/data.xml").toURI()
                            )
                    );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}