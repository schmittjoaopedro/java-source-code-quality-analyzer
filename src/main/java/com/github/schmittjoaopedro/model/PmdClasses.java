package com.github.schmittjoaopedro.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class PmdClasses {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private double count;

    public PmdClasses() {
        super();
    }

    public PmdClasses(String name, double count) {
        this.name = name;
        this.count = count;
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

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }
}
