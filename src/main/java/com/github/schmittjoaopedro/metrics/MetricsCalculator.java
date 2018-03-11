package com.github.schmittjoaopedro.metrics;

public final class MetricsCalculator {

    public static void calculate(Metrics metrics) {
        // Check Style
        double checkStyleSum = 0.0;
        for (CheckstyleMetrics checkStyle : metrics.getCheckstyleMetrics()) {
            checkStyleSum += checkStyle.getSeverityLevel();
        }
        metrics.setCheckStyleComplexity(checkStyleSum);
        // PMD
        double pmdSum = 0.0;
        for(PMDMetrics pmd : metrics.getPmdMetrics()) {
            pmdSum += pmd.getPriority();
        }
        metrics.setPmdComplexity(pmdSum);
        // SpotBugs
        double spotBugsSum = 0.0;
        for(SpotBugsMetrics spotBugs : metrics.getSpotBugsMetrics()) {
            spotBugsSum += spotBugs.getPriority();
        }
        metrics.setSpotBugsComplexity(spotBugsSum);
        // Calculate class complexity
        metrics.setClassComplexity(checkStyleSum * 1.0 + pmdSum * 2.0 + spotBugsSum * 4.0);
    }

}
