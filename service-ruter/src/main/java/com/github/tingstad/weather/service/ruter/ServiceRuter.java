package com.github.tingstad.weather.service.ruter;

import com.github.tingstad.weather.service.api.Service;
import com.github.tingstad.weather.service.api.Status;
import com.github.tingstad.weather.service.api.TimeProvider;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.function.Supplier;

/**
 *
 * <3 Dependency Inversion <3
 *
 * Gets data from dataSource InputStream and extracts
 * any messages for a given line number affected by any
 * situations this day.
 * <p>
 * Expects data to be of the format explained here:
 * http://sirisx.ruter.no/help
 */
public class ServiceRuter implements Service {

    private final Supplier<InputStream> dataSource;
    private final TimeProvider timeProvider;
    private final int lineNumber;

    public ServiceRuter(Supplier<InputStream> dataSource, TimeProvider timeProvider, int lineNumber) {
        this.dataSource = dataSource;
        this.lineNumber = lineNumber;
        this.timeProvider = timeProvider;
    }

    @Override
    public Status getStatus() {
        String text = getText();
        return Status.create(text, Status.Severity.LOW);
    }

    private String getText() {
        try (InputStream inputStream = dataSource.get()) {
            return process(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String process(InputStream inputStream) throws Exception {
        StreamSource stylesource = new StreamSource(
                ServiceRuter.class.getResourceAsStream("/transform.xsl"));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(stylesource);
        transformer.setParameter("lineNo", lineNumber);
        transformer.setParameter("date", timeProvider.getTime().toLocalDate().toString());
        StringWriter stringWriter = new StringWriter();
        transformer.transform(new StreamSource(inputStream), new StreamResult(stringWriter));

        return stringWriter.toString();
    }

}
