package com.github.schmittjoaopedro.metrics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Metrics implements Serializable {

    private List<PMDMetrics> pmdMetrics;

    private List<SpotBugsMetrics> spotBugsMetrics;

    public Metrics() {
        super();
    }

    public List<SpotBugsMetrics> getSpotBugsMetrics() {
        if(spotBugsMetrics == null) spotBugsMetrics = new ArrayList<>();
        return spotBugsMetrics;
    }

    public void setSpotBugsMetrics(List<SpotBugsMetrics> spotBugsMetrics) {
        this.spotBugsMetrics = spotBugsMetrics;
    }

    public List<PMDMetrics> getPmdMetrics() {
        if(pmdMetrics == null) pmdMetrics = new ArrayList<>();
        return pmdMetrics;
    }

    public void setPmdMetrics(List<PMDMetrics> pmdMetrics) {
        this.pmdMetrics = pmdMetrics;
    }
}
