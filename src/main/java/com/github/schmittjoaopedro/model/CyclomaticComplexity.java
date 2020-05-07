package com.github.schmittjoaopedro.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class CyclomaticComplexity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String methodName;

    private int cyclomatic;

    public CyclomaticComplexity() {
        super();
    }

    public CyclomaticComplexity(String methodName, int cyclomatic) {
        this.methodName = methodName;
        this.cyclomatic = cyclomatic;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public int getCyclomatic() {
        return cyclomatic;
    }

    public void setCyclomatic(int cyclomatic) {
        this.cyclomatic = cyclomatic;
    }
}
