package com.github.tingstad.weather.service.yr;

import com.github.tingstad.weather.service.api.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Double.parseDouble;
import static java.lang.Math.round;

public class ServiceYr implements Service {

    private final DataSource dataSource;
    private final Map<LocalDateTime, String> cache = new HashMap<>();

    public ServiceYr() {
        this(new YrDataSource());
    }

    ServiceYr(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String getText() {
        return cache.keySet().stream()
                .filter(time -> time.plusMinutes(5).isAfter(LocalDateTime.now()))
                .findFirst()
                .map(key -> cache.get(key)).orElseGet(() -> {
                    String value = getValue();
                    cache.clear();
                    cache.put(LocalDateTime.now(), value);
                    return value;
                });
    }

    private String getValue() {
        try (InputStream inputStream = dataSource.getData()) {
            return process(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String process(InputStream inputStream) throws Exception {

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(inputStream);

        XPath xpath = XPathFactory.newInstance().newXPath();
        String expression = "/weatherdata/forecast/tabular/time[@period='2']";
        Node periode = xpath.evaluateExpression(expression, document, Node.class);
        String symbol = xpath.evaluateExpression(
                "symbol/@*", periode, Node.class).getTextContent();
        String precipitation = xpath.evaluateExpression(
                "precipitation/@value", periode, Node.class).getTextContent();
        String windSpeed = xpath.evaluateExpression(
                "windSpeed/@name", periode, Node.class).getTextContent();
        String temperature = xpath.evaluateExpression(
                "temperature/@value", periode, Node.class).getTextContent();
        return String.format("%s, %smm, %s, %s grader",
                symbol,
                round(parseDouble(precipitation)),
                windSpeed,
                temperature
        );
    }

}
