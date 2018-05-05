package com.github.schmittjoaopedro.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Statistics implements Serializable {

    private double statistic;

    private long linesNumber;

    private long notEmptyLinesNumber;

    private long uncommentedLinesNumber;

    private long normalizedLinesNumber;

    private double entropy;

    private long pmdViolations;

    private long checkStyleViolations;

    private Map<String, Double> pmdClasses;

    private Map<String, Double> pmdWeightedClasses;

    private Map<String, Double> checkStyleClasses;

    private int complexityHigh;

    private int complexityLow;

    private double complexityMean;

    private int complexityClass;

    private double position;

    private int numberOfViolations;

    private double violationsWeightedTotal;

    private double violationsWeightedMean;

    public Statistics() {
        super();
    }

    public double getStatistic() {
        return statistic;
    }

    public void setStatistic(double statistic) {
        this.statistic = statistic;
    }

    public Map<String, Double> getPmdWeightedClasses() {
        if(pmdWeightedClasses == null) pmdWeightedClasses = new HashMap<>();
        return pmdWeightedClasses;
    }

    public void setPmdWeightedClasses(Map<String, Double> pmdWeightedClasses) {
        this.pmdWeightedClasses = pmdWeightedClasses;
    }

    public Map<String, Double> getPmdClasses() {
        if(pmdClasses == null) pmdClasses = new HashMap<>();
        return pmdClasses;
    }

    public void setPmdClasses(Map<String, Double> pmdClasses) {
        this.pmdClasses = pmdClasses;
    }

    public Map<String, Double> getCheckStyleClasses() {
        if(checkStyleClasses == null) checkStyleClasses = new HashMap<>();
        return checkStyleClasses;
    }

    public void setCheckStyleClasses(Map<String, Double> checkStyleClasses) {
        this.checkStyleClasses = checkStyleClasses;
    }

    public long getLinesNumber() {
        return linesNumber;
    }

    public void setLinesNumber(long linesNumber) {
        this.linesNumber = linesNumber;
    }

    public long getNotEmptyLinesNumber() {
        return notEmptyLinesNumber;
    }

    public void setNotEmptyLinesNumber(long notEmptyLinesNumber) {
        this.notEmptyLinesNumber = notEmptyLinesNumber;
    }

    public long getUncommentedLinesNumber() {
        return uncommentedLinesNumber;
    }

    public void setUncommentedLinesNumber(long uncommentedLinesNumber) {
        this.uncommentedLinesNumber = uncommentedLinesNumber;
    }

    public long getNormalizedLinesNumber() {
        return normalizedLinesNumber;
    }

    public void setNormalizedLinesNumber(long normalizedLinesNumber) {
        this.normalizedLinesNumber = normalizedLinesNumber;
    }

    public double getEntropy() {
        return entropy;
    }

    public void setEntropy(double entropy) {
        this.entropy = entropy;
    }

    public long getPmdViolations() {
        return pmdViolations;
    }

    public void setPmdViolations(long pmdViolations) {
        this.pmdViolations = pmdViolations;
    }

    public long getCheckStyleViolations() {
        return checkStyleViolations;
    }

    public void setCheckStyleViolations(long checkStyleViolations) {
        this.checkStyleViolations = checkStyleViolations;
    }

    public int getComplexityHigh() {
        return complexityHigh;
    }

    public void setComplexityHigh(int complexityHigh) {
        this.complexityHigh = complexityHigh;
    }

    public int getComplexityLow() {
        return complexityLow;
    }

    public void setComplexityLow(int complexityLow) {
        this.complexityLow = complexityLow;
    }

    public double getComplexityMean() {
        return complexityMean;
    }

    public void setComplexityMean(double complexityMean) {
        this.complexityMean = complexityMean;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public int getComplexityClass() {
        return complexityClass;
    }

    public void setComplexityClass(int complexityClass) {
        this.complexityClass = complexityClass;
    }

    public int getNumberOfViolations() {
        return numberOfViolations;
    }

    public void setNumberOfViolations(int numberOfViolations) {
        this.numberOfViolations = numberOfViolations;
    }

    public double getViolationsWeightedTotal() {
        return violationsWeightedTotal;
    }

    public void setViolationsWeightedTotal(double violationsWeightedTotal) {
        this.violationsWeightedTotal = violationsWeightedTotal;
    }

    public double getViolationsWeightedMean() {
        return violationsWeightedMean;
    }

    public void setViolationsWeightedMean(double violationsWeightedMean) {
        this.violationsWeightedMean = violationsWeightedMean;
    }
}
