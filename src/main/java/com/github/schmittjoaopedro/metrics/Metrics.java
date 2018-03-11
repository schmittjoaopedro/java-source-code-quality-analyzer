package com.github.schmittjoaopedro.metrics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Metrics implements Serializable {

    private String className;

    private List<PMDMetrics> pmdMetrics;

    private List<SpotBugsMetrics> spotBugsMetrics;

    private List<CheckstyleMetrics> checkstyleMetrics;

    private double pmdComplexity;

    private double checkStyleComplexity;

    private double spotBugsComplexity;

    private double classComplexity;

    public Metrics() {
        super();
    }

    public List<SpotBugsMetrics> getSpotBugsMetrics() {
        if (spotBugsMetrics == null) spotBugsMetrics = new ArrayList<>();
        return spotBugsMetrics;
    }

    public void setSpotBugsMetrics(List<SpotBugsMetrics> spotBugsMetrics) {
        this.spotBugsMetrics = spotBugsMetrics;
    }

    public List<PMDMetrics> getPmdMetrics() {
        if (pmdMetrics == null) pmdMetrics = new ArrayList<>();
        return pmdMetrics;
    }

    public void setPmdMetrics(List<PMDMetrics> pmdMetrics) {
        this.pmdMetrics = pmdMetrics;
    }

    public List<CheckstyleMetrics> getCheckstyleMetrics() {
        if (checkstyleMetrics == null) checkstyleMetrics = new ArrayList<>();
        return checkstyleMetrics;
    }

    public void setCheckstyleMetrics(List<CheckstyleMetrics> checkstyleMetrics) {
        this.checkstyleMetrics = checkstyleMetrics;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public double getPmdComplexity() {
        return pmdComplexity;
    }

    public void setPmdComplexity(double pmdComplexity) {
        this.pmdComplexity = pmdComplexity;
    }

    public double getCheckStyleComplexity() {
        return checkStyleComplexity;
    }

    public void setCheckStyleComplexity(double checkStyleComplexity) {
        this.checkStyleComplexity = checkStyleComplexity;
    }

    public double getSpotBugsComplexity() {
        return spotBugsComplexity;
    }

    public void setSpotBugsComplexity(double spotBugsComplexity) {
        this.spotBugsComplexity = spotBugsComplexity;
    }

    public double getClassComplexity() {
        return classComplexity;
    }

    public void setClassComplexity(double classComplexity) {
        this.classComplexity = classComplexity;
    }
}
