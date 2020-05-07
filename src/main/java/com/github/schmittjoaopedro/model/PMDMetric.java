package com.github.schmittjoaopedro.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;
import java.nio.charset.Charset;

@Entity
public class PMDMetric implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;

    @Id
    @GeneratedValue
    private Long id;

	private int beginLine;

    private int endLine;

    @Lob
    private byte[] description;

    private int beginColumn;

    private int endColumn;

    @Lob
    private byte[] rule;

    @Lob
    private byte[] message;

    private int priority;

    @Lob
    private byte[] uriInfo;

    public PMDMetric() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return new String(description, Charset.forName("UTF-8"));
    }

    public void setDescription(String description) {
        this.description = description.getBytes(Charset.forName("UTF-8"));
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
        return new String(rule, Charset.forName("UTF-8"));
    }

    public void setRule(String rule) {
        this.rule = rule.getBytes(Charset.forName("UTF-8"));
    }

    public String getMessage() {
        return new String(message, Charset.forName("UTF-8"));
    }

    public void setMessage(String message) {
        this.message = message.getBytes(Charset.forName("UTF-8"));
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getUriInfo() {
        return new String(uriInfo, Charset.forName("UTF-8"));
    }

    public void setUriInfo(String uriInfo) {
        this.uriInfo = uriInfo.getBytes(Charset.forName("UTF-8"));
    }
}
