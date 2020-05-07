package com.github.schmittjoaopedro.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class Statistics implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private double statistic;

    private long linesNumber;

    private long notEmptyLinesNumber;

    private long uncommentedLinesNumber;

    private long normalizedLinesNumber;

    private double entropy;

    private long pmdViolations;

    private long checkStyleViolations;

    @Fetch(value = FetchMode.SELECT)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "PMD_CLASSES_ID")
    private List<PmdClasses> pmdClasses;

    @Fetch(value = FetchMode.SELECT)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "PMD_WEIGHT_CLASSES_ID")
    private List<PmdWeightedClasses> pmdWeightedClasses;

    @Fetch(value = FetchMode.SELECT)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "CHECKSTYLE_CLASSES_ID")
    private List<CheckStyleClasses> checkStyleClasses;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getStatistic() {
        return statistic;
    }

    public void setStatistic(double statistic) {
        this.statistic = statistic;
    }

    public List<PmdWeightedClasses> getPmdWeightedClasses() {
        if (pmdWeightedClasses == null) pmdWeightedClasses = new ArrayList<>();
        return pmdWeightedClasses;
    }

    public Map<String, Double> getPmdWeightedClassesAsMap() {
        return getPmdWeightedClasses().stream().collect(Collectors.toMap(PmdWeightedClasses::getName, PmdWeightedClasses::getCount));
    }

    public List<PmdClasses> getPmdClasses() {
        if (pmdClasses == null) pmdClasses = new ArrayList<>();
        return pmdClasses;
    }

    public Map<String, Double> getPmdClassesAsMap() {
        return getPmdClasses().stream().collect(Collectors.toMap(PmdClasses::getName, PmdClasses::getCount));
    }

    public List<CheckStyleClasses> getCheckStyleClasses() {
        if (checkStyleClasses == null) checkStyleClasses = new ArrayList<>();
        return checkStyleClasses;
    }

    public Map<String, Double> getCheckStyleClassesAsMap() {
        return getCheckStyleClasses().stream().collect(Collectors.toMap(CheckStyleClasses::getName, CheckStyleClasses::getCount));
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
