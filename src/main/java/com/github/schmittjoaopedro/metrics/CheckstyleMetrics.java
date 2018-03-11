package com.github.schmittjoaopedro.metrics;

import java.io.Serializable;

public class CheckstyleMetrics implements Serializable {

    private int line;

    private String description;

    private int severityLevel;

    public CheckstyleMetrics() {
        super();
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(int severityLevel) {
        this.severityLevel = severityLevel;
    }
}
