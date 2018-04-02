package com.github.schmittjoaopedro.dto;

import java.util.Date;

public class MetricHeader {

    private String id;

    private Long ruleVersionId;

    private String userCreated;

    private String userUpdated;

    private Date dateCreated;

    private Date dateUpdated;

    private Double classComplexity;

    public MetricHeader() {
        super();
    }

    public MetricHeader(String id, Long ruleVersionId, String userCreated, String userUpdated, Date dateCreated, Date dateUpdated, Double classComplexity) {
        super();
        this.id = id;
        this.ruleVersionId = ruleVersionId;
        this.userCreated = userCreated;
        this.userUpdated = userUpdated;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.classComplexity = classComplexity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getRuleVersionId() {
        return ruleVersionId;
    }

    public void setRuleVersionId(Long ruleVersionId) {
        this.ruleVersionId = ruleVersionId;
    }

    public String getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(String userCreated) {
        this.userCreated = userCreated;
    }

    public String getUserUpdated() {
        return userUpdated;
    }

    public void setUserUpdated(String userUpdated) {
        this.userUpdated = userUpdated;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Double getClassComplexity() {
        return classComplexity;
    }

    public void setClassComplexity(Double classComplexity) {
        this.classComplexity = classComplexity;
    }
}
