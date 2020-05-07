package com.github.schmittjoaopedro.model;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Metric implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue
    private Long id;

	private String name;

    private String error;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Statistics statistics;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private SourceCode sourceCode;

    private boolean pmd;

    private boolean checkStyle;

    private boolean cyclomaticComplexity;

    @Fetch(value = FetchMode.SELECT)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "PMD_METRICS_ID")
    private List<PMDMetric> pmdMetrics;

    @Fetch(value = FetchMode.SELECT)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "CHECKSTYLE_METRICS_ID")
    private List<CheckstyleMetric> checkstyleMetrics;

    @Fetch(value = FetchMode.SELECT)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "CYCLOMATIC_COMPLEXITY_METRICS_ID")
    private List<CyclomaticComplexity> cyclomaticComplexities;

    public Metric() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

    public boolean isCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    public void setCyclomaticComplexity(boolean cyclomaticComplexity) {
        this.cyclomaticComplexity = cyclomaticComplexity;
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

    public List<CyclomaticComplexity> getCyclomaticComplexities() {
        if(cyclomaticComplexities == null) {
            cyclomaticComplexities = new ArrayList<>();
        }
        return cyclomaticComplexities;
    }

    public void setCyclomaticComplexities(List<CyclomaticComplexity> cyclomaticComplexities) {
        this.cyclomaticComplexities = cyclomaticComplexities;
    }

    public String getSource() {
        if(this.getSourceCode() == null || this.getSourceCode().getSourceCode() == null) {
            return StringUtils.EMPTY;
        } else {
            return this.getSourceCode().getSourceCode();
        }
    }

}
