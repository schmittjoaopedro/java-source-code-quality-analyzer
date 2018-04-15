package com.github.schmittjoaopedro.model;

import java.io.Serializable;

public class CyclomaticComplexity implements Serializable {

    private String methodName;

    private int cyclomatic;

    public CyclomaticComplexity() {
        super();
    }

    public CyclomaticComplexity(String methodName, int cyclomatic) {
        this.methodName = methodName;
        this.cyclomatic = cyclomatic;
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
