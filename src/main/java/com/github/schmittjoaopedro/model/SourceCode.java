package com.github.schmittjoaopedro.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Date;

@Entity
public class SourceCode implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    private Long name;

    private String description;

    private String userCreated;

    private Date dateCreated;

    private String className;

    @Lob
    private byte[] sourceCode;

    public SourceCode() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSourceCode() {
        return new String(sourceCode, Charset.forName("UTF-8"));
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode.getBytes(Charset.forName("UTF-8"));
    }

}
