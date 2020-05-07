package com.github.schmittjoaopedro.analyser;

import com.github.schmittjoaopedro.model.Metric;
import com.github.schmittjoaopedro.model.PmdClasses;
import com.github.schmittjoaopedro.model.PmdWeightedClasses;
import com.github.schmittjoaopedro.model.Statistics;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class MetricCalculator {

    private static final String IMPLEMENTS = "implements";
    private static final String EXTENDS = "extends";
    private static final String BRACKET = "{";
    private static final String CLASS = "class";
    private static final String PRIVATE = "private";
    private static final String PUBLIC = "public";
    private static final String CLASS_REG_EXP = "\\s*(public|private)\\s+class\\s+(\\w+)\\s+((extends\\s+\\w+)|(implements\\s+\\w+( ,\\w+)*))?\\s*\\{";

    public static void calculate(Metric metric) {
        Statistics statistics = new Statistics();

        statistics.setLinesNumber(getLinesNumber(metric.getSource()));
        statistics.setNormalizedLinesNumber(getNormalizedLinesNumber(metric.getSource()));
        statistics.setUncommentedLinesNumber(getUncommentedLinesNumber(metric.getSource()));
        statistics.setNotEmptyLinesNumber(getNotEmptyLinesNumber(metric.getSource()));

        getMetricsCounts(metric, statistics);
        Double entropy = Math.max(1.0, getEntropy(statistics));
        statistics.setEntropy(entropy);

        getComplexityMetrics(metric, statistics);

        metric.setStatistics(statistics);
    }

    private static void getComplexityMetrics(Metric metric, Statistics statistics) {
        if (!metric.getCyclomaticComplexities().isEmpty()) {
            IntSummaryStatistics stats = metric.getCyclomaticComplexities()
                    .stream()
                    .mapToInt(x -> x.getCyclomatic())
                    .summaryStatistics();
            statistics.setComplexityHigh(stats.getMax());
            statistics.setComplexityLow(stats.getMin());
            statistics.setComplexityMean(stats.getAverage());
            statistics.setComplexityClass(4);
            if (statistics.getComplexityHigh() <= 4) {
                statistics.setComplexityClass(1);
            } else if (statistics.getComplexityHigh() <= 7) {
                statistics.setComplexityClass(2);
            } else if (statistics.getComplexityHigh() <= 10) {
                statistics.setComplexityClass(3);
            }
            statistics.setStatistic(statistics.getEntropy() * statistics.getComplexityClass());

            Map<String, Double> pmdWeightedMap = statistics.getPmdWeightedClassesAsMap();
            DoubleSummaryStatistics statsWeighted = pmdWeightedMap.values()
                    .stream()
                    .mapToDouble(x -> x)
                    .summaryStatistics();
            statistics.setNumberOfViolations(pmdWeightedMap.keySet().size());
            statistics.setViolationsWeightedTotal(statsWeighted.getSum());
            statistics.setViolationsWeightedMean(statsWeighted.getAverage());
        }
    }

    private static void getMetricsCounts(Metric metric, Statistics statistics) {
        statistics.setPmdViolations(metric.getPmdMetrics().size());
        statistics.setCheckStyleViolations(metric.getCheckstyleMetrics().size());
        // PMD Classes
        Map<String, Double> pmdClasses = new HashMap<>();
        metric.getPmdMetrics().forEach(pmd -> {
            if (!pmdClasses.containsKey(pmd.getName())) {
                pmdClasses.put(pmd.getName(), 1.0);
            } else {
                pmdClasses.put(pmd.getName(), 1.0 + pmdClasses.get(pmd.getName()));
            }
        });
        pmdClasses.forEach((key, value) -> statistics.getPmdClasses().add(new PmdClasses(key, value)));

        // Weighted Classes
        Map<String, Double> pmdWeightedClasses = new HashMap<>();
        metric.getPmdMetrics().forEach(pmd -> {
            if (!pmdWeightedClasses.containsKey(pmd.getName())) {
                pmdWeightedClasses.put(pmd.getName(), (double) pmd.getPriority());
            } else {
                pmdWeightedClasses.put(pmd.getName(), pmd.getPriority() + pmdWeightedClasses.get(pmd.getName()));
            }
        });
        Map<String, Double> checkStyleClasses = new HashMap<>();
        metric.getCheckstyleMetrics().forEach(checkStyle -> {
            if (!checkStyleClasses.containsKey(checkStyle.getName())) {
                checkStyleClasses.put(checkStyle.getName(), 1.0);
            } else {
                checkStyleClasses.put(checkStyle.getName(), 1.0 + checkStyleClasses.get(checkStyle.getName()));
            }
        });
        pmdWeightedClasses.put("CheckStyle", checkStyleClasses.values().stream().mapToDouble(Double::doubleValue).sum());
        pmdWeightedClasses.forEach((key, value) -> statistics.getPmdWeightedClasses().add(new PmdWeightedClasses(key, value)));
    }

    private static double getEntropy(Statistics statistics) {
        final Map<String, Double> count = new HashMap<>();
        statistics.getPmdWeightedClassesAsMap().forEach((key, value) -> {
            count.put(key, Math.log(value + .1));
        });
        double total = count.values().stream().mapToDouble(Double::doubleValue).sum();
        List<Double> probabilities = count.values().stream().map(item -> item / total).collect(Collectors.toList());
        double entropy = 0.0;
        for (double probability : probabilities) {
            entropy += probability * (Math.log10(probability) / Math.log10(2));
        }
        return entropy * -1.0;
    }

    private static long getLinesNumber(String sourceCode) {
        return Arrays.stream(sourceCode.split("\n")).count();
    }

    public static long getNotEmptyLinesNumber(String sourceCode) {
        return Arrays.stream(sourceCode.split("\n")).map(item -> item.replace("\r", StringUtils.EMPTY).trim()).filter(item -> !item.isEmpty()).count();
    }

    public static long getUncommentedLinesNumber(String sourceCode) {
        String sourceCodeFiltered = sourceCode.replaceAll("((['\"])(?:(?!\\2|\\\\).|\\\\.)*\\2)|\\/\\/[^\\n]*|\\/\\*(?:[^*]|\\*(?!\\/))*\\*\\/", StringUtils.EMPTY);
        return getLinesNumber(sourceCodeFiltered);
    }

    public static long getNormalizedLinesNumber(String sourceCode) {
        String sourceCodeFiltered = sourceCode.replaceAll("((['\"])(?:(?!\\2|\\\\).|\\\\.)*\\2)|\\/\\/[^\\n]*|\\/\\*(?:[^*]|\\*(?!\\/))*\\*\\/", StringUtils.EMPTY);
        return getNotEmptyLinesNumber(sourceCodeFiltered);
    }

    public static String extractClassNameFromSourceCode(String sourceCode) {
        String className = null;
        Pattern classPatter = Pattern.compile(CLASS_REG_EXP);
        Matcher m = classPatter.matcher(sourceCode);
        while (m.find()) {
            className = m.group(0);
            className = className.replace(CLASS, "").replace(BRACKET, "");
            if (className.contains(PUBLIC)) className = className.replace(PUBLIC, "");
            if (className.contains(PRIVATE)) className = className.replace(PRIVATE, "");
            if (className.contains(EXTENDS))
                className = className.replace(className.substring(className.indexOf(EXTENDS)), "");
            if (className.contains(IMPLEMENTS))
                className = className.replace(className.substring(className.indexOf(IMPLEMENTS)), "");
            className = className.trim();
            break;
        }
        return className;

    }

}
