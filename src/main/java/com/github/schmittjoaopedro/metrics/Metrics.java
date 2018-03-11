package com.github.schmittjoaopedro.metrics;

import java.io.Serializable;

public class Metrics implements Serializable {

    private PMDMetrics pmdMetrics;

    private SpotBugsMetrics spotBugsMetrics;

    public Metrics() {
        super();
    }

    public PMDMetrics getPmdMetrics() {
        return pmdMetrics;
    }

    public void setPmdMetrics(PMDMetrics pmdMetrics) {
        this.pmdMetrics = pmdMetrics;
    }

    public SpotBugsMetrics getSpotBugsMetrics() {
        return spotBugsMetrics;
    }

    public void setSpotBugsMetrics(SpotBugsMetrics spotBugsMetrics) {
        this.spotBugsMetrics = spotBugsMetrics;
    }
}
