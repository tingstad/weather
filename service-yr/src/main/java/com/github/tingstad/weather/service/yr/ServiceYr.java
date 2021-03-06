package com.github.tingstad.weather.service.yr;

import com.github.tingstad.weather.service.api.Service;
import com.github.tingstad.weather.service.api.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.function.Supplier;

import static java.lang.Double.parseDouble;
import static java.lang.Math.round;

/**
 * Gets data from dataSource InputStream and transforms it into
 * readable weather forecast text.
 *
 * Expects data to be of the following format:
 * http://om.yr.no/verdata/xml/spesifikasjon/
 */
public class ServiceYr implements Service {

    private final Supplier<InputStream> dataSource;

    public ServiceYr(Supplier<InputStream> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Status getStatus() {
        return Status.create(getText(), Status.Severity.LOW);
    }

    private String getText() {
        try {
            return getValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getValue() throws Exception {
        final Document document;
        try (InputStream inputStream = dataSource.get()) {
            document = parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return process(document);
    }

    private Document parse(InputStream inputStream) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(inputStream);
        return document;
    }

    private String process(Document document) throws Exception {

        XPath xpath = XPathFactory.newInstance().newXPath();
        String timezone = xpath.evaluateExpression(
                "/weatherdata/location/timezone/@id", document, Node.class).getTextContent();
        Node periode = xpath.evaluateExpression(
                "/weatherdata/forecast/tabular/time[@period='2']", document, Node.class);
        String from = xpath.evaluateExpression(
                "@from", periode, Node.class).getTextContent();
        String to = xpath.evaluateExpression(
                "@to", periode, Node.class).getTextContent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        String fromString = sdf.parse(from).toInstant().atZone(ZoneId.of(timezone))
                .format(DateTimeFormatter.ofPattern("dd/MM HH"));
        String toString = sdf.parse(to).toInstant().atZone(ZoneId.of(timezone))
                .format(DateTimeFormatter.ofPattern("HH"));
        String symbol = xpath.evaluateExpression(
                "symbol/@name", periode, Node.class).getTextContent();
        String precipitation = xpath.evaluateExpression(
                "precipitation/@value", periode, Node.class).getTextContent();
        String windSpeed = xpath.evaluateExpression(
                "windSpeed/@name", periode, Node.class).getTextContent();
        String temperature = xpath.evaluateExpression(
                "temperature/@value", periode, Node.class).getTextContent();
        return String.format("%s-%s: %s, %smm, %s, %s grader",
                fromString,
                toString,
                symbol,
                round(parseDouble(precipitation)),
                windSpeed,
                temperature
        );
    }

}
