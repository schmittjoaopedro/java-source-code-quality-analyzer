package com.github.schmittjoaopedro.model;

import java.io.Serializable;

public class PMDMetric implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;

	private int beginLine;

    private int endLine;

    private String description;

    private int beginColumn;

    private int endColumn;

    private String rule;

    private String message;

    private int priority;

    private String uriInfo;

    public PMDMetric() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBeginLine() {
        return beginLine;
    }

    public void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBeginColumn() {
        return beginColumn;
    }

    public void setBeginColumn(int beginColumn) {
        this.beginColumn = beginColumn;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(int endColumn) {
        this.endColumn = endColumn;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getUriInfo() {
        return uriInfo;
    }

    public void setUriInfo(String uriInfo) {
        this.uriInfo = uriInfo;
    }
}
