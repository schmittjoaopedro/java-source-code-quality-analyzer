package com.github.schmittjoaopedro.metrics;

import java.io.Serializable;

public class SpotBugsMetrics implements Serializable {

    private String message;

    private String category;

    private int rank;

    private int priority;

    private String priorityCategory;

    public SpotBugsMetrics() {
        super();
    }

    public SpotBugsMetrics(String message, String category, int rank, int priority, String priorityCategory) {
        super();
        this.message = message;
        this.category = category;
        this.rank = rank;
        this.priority = priority;
        this.priorityCategory = priorityCategory;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getPriorityCategory() {
        return priorityCategory;
    }

    public void setPriorityCategory(String priorityCategory) {
        this.priorityCategory = priorityCategory;
    }
}