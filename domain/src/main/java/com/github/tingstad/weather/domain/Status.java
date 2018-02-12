package com.github.tingstad.weather.domain;

public class Status {

    public enum Priority implements Comparable<Priority> {
        LOW,
        NORMAL,
        HIGH
    }
    private final String text;
    private final Priority priority;

    public Status(String text, Priority priority) {
        this.text = text;
        this.priority = priority;
    }

    public String getText() {
        return text;
    }

    public Priority getPriority() {
        return priority;
    }

}
