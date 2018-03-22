package com.github.schmittjoaopedro.model;

import java.io.Serializable;

public class SourceCode implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sourceCode;

    private boolean pmd;

    private boolean spotBugs;

    private boolean checkStyle;

    private String user;

    public SourceCode() {
        super();
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public boolean isPmd() {
        return pmd;
    }

    public void setPmd(boolean pmd) {
        this.pmd = pmd;
    }

    public boolean isSpotBugs() {
        return spotBugs;
    }

    public void setSpotBugs(boolean spotBugs) {
        this.spotBugs = spotBugs;
    }

    public boolean isCheckStyle() {
        return checkStyle;
    }

    public void setCheckStyle(boolean checkStyle) {
        this.checkStyle = checkStyle;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
