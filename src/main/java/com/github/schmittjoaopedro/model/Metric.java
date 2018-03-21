package com.github.schmittjoaopedro.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Metric implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private Long ruleId;
	
	private String sourceCode;

    private String className;

    private String error;

    private double classComplexity;

    private double pmdComplexity;

    private double checkStyleComplexity;

    private double spotBugsComplexity;

    private List<PMDMetric> pmdMetrics;

    private List<SpotBugsMetric> spotBugsMetrics;

    private List<CheckstyleMetric> checkstyleMetrics;

    public Metric() {
        super();
    }

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getRuleId() {
		return ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public List<SpotBugsMetric> getSpotBugsMetrics() {
        if (spotBugsMetrics == null) spotBugsMetrics = new ArrayList<>();
        return spotBugsMetrics;
    }

    public void setSpotBugsMetrics(List<SpotBugsMetric> spotBugsMetrics) {
        this.spotBugsMetrics = spotBugsMetrics;
    }

    public List<PMDMetric> getPmdMetrics() {
        if (pmdMetrics == null) pmdMetrics = new ArrayList<>();
        return pmdMetrics;
    }

    public void setPmdMetrics(List<PMDMetric> pmdMetrics) {
        this.pmdMetrics = pmdMetrics;
    }

    public List<CheckstyleMetric> getCheckstyleMetrics() {
        if (checkstyleMetrics == null) checkstyleMetrics = new ArrayList<>();
        return checkstyleMetrics;
    }

    public void setCheckstyleMetrics(List<CheckstyleMetric> checkstyleMetrics) {
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
