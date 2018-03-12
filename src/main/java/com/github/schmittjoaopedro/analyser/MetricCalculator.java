package com.github.schmittjoaopedro.analyser;

import com.github.schmittjoaopedro.model.CheckstyleMetric;
import com.github.schmittjoaopedro.model.Metric;
import com.github.schmittjoaopedro.model.PMDMetric;
import com.github.schmittjoaopedro.model.SpotBugsMetric;

public final class MetricCalculator {

    public static void calculate(Metric metric) {
        // Check Style
        double checkStyleSum = 0.0;
        for (CheckstyleMetric checkStyle : metric.getCheckstyleMetrics()) {
            checkStyleSum += checkStyle.getSeverityLevel();
        }
        metric.setCheckStyleComplexity(checkStyleSum);
        // PMD
        double pmdSum = 0.0;
        for(PMDMetric pmd : metric.getPmdMetrics()) {
            pmdSum += pmd.getPriority();
        }
        metric.setPmdComplexity(pmdSum);
        // SpotBugs
        double spotBugsSum = 0.0;
        for(SpotBugsMetric spotBugs : metric.getSpotBugsMetrics()) {
            spotBugsSum += spotBugs.getPriority();
        }
        metric.setSpotBugsComplexity(spotBugsSum);
        // Calculate class complexity
        metric.setClassComplexity(checkStyleSum * 1.0 + pmdSum * 2.0 + spotBugsSum * 4.0);
    }

}
