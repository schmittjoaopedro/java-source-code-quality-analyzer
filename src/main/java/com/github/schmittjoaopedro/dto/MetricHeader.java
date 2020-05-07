package com.github.schmittjoaopedro.dto;

import java.util.Date;

public class MetricHeader {

    private String name;

    private String userCreated;

    private Date dateCreated;

    private Double classComplexity;

    public MetricHeader() {
        super();
    }

    public MetricHeader(String name, String userCreated, Date dateCreated, Double classComplexity) {
        this.name = name;
        this.userCreated = userCreated;
        this.dateCreated = dateCreated;
        this.classComplexity = classComplexity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(String userCreated) {
        this.userCreated = userCreated;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Double getClassComplexity() {
        return classComplexity;
    }

    public void setClassComplexity(Double classComplexity) {
        this.classComplexity = classComplexity;
    }
}
