package com.github.schmittjoaopedro.model;

import java.io.Serializable;

public class SourceCode implements Serializable {

    private String sourceCode;

    public SourceCode() {
        super();
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
}
