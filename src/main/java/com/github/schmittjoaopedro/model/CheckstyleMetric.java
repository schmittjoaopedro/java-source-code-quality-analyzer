package com.github.schmittjoaopedro.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;
import java.nio.charset.Charset;

@Entity
public class CheckstyleMetric implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

	private int line;

	@Lob
	private byte[] name;

    @Lob
    private byte[] description;

    private int severityLevel;

    public CheckstyleMetric() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return new String(name, Charset.forName("UTF-8"));
    }

    public void setName(String name) {
        this.name = name.getBytes(Charset.forName("UTF-8"));
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getDescription() {
        return new String(description, Charset.forName("UTF-8"));
    }

    public void setDescription(String description) {
        this.description = description.getBytes(Charset.forName("UTF-8"));
    }

    public int getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(int severityLevel) {
        this.severityLevel = severityLevel;
    }
}
