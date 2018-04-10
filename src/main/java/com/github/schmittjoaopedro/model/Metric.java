package com.github.schmittjoaopedro.model;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Metric implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

    private String error;

    private Statistics statistics;

    private SourceCode sourceCode;

    private boolean pmd;

    private boolean checkStyle;

    private List<PMDMetric> pmdMetrics;

    private List<CheckstyleMetric> checkstyleMetrics;

    public Metric() {
        super();
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public SourceCode getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(SourceCode sourceCode) {
        this.sourceCode = sourceCode;
    }

    public boolean isPmd() {
        return pmd;
    }

    public void setPmd(boolean pmd) {
        this.pmd = pmd;
    }

    public boolean isCheckStyle() {
        return checkStyle;
    }

    public void setCheckStyle(boolean checkStyle) {
        this.checkStyle = checkStyle;
    }

    public List<PMDMetric> getPmdMetrics() {
        if(pmdMetrics == null) pmdMetrics = new ArrayList<>();
        return pmdMetrics;
    }

    public void setPmdMetrics(List<PMDMetric> pmdMetrics) {
        this.pmdMetrics = pmdMetrics;
    }

    public List<CheckstyleMetric> getCheckstyleMetrics() {
        if(checkstyleMetrics == null) checkstyleMetrics = new ArrayList<>();
        return checkstyleMetrics;
    }

    public void setCheckstyleMetrics(List<CheckstyleMetric> checkstyleMetrics) {
        this.checkstyleMetrics = checkstyleMetrics;
    }

    public String getSource() {
        if(this.getSourceCode() == null || this.getSourceCode().getSourceCode() == null) {
            return StringUtils.EMPTY;
        } else {
            return this.getSourceCode().getSourceCode();
        }
    }
}
