package com.github.schmittjoaopedro.analyser;

import com.github.schmittjoaopedro.model.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        statistics.setLinesNumber(getLinesNumber(metric.getSourceCode().getSourceCode()));
        statistics.setNormalizedLinesNumber(getNormalizedLinesNumber(metric.getSourceCode().getSourceCode()));
        statistics.setUncommentedLinesNumber(getUncommentedLinesNumber(metric.getSourceCode().getSourceCode()));
        statistics.setNotEmptyLinesNumber(getNotEmptyLinesNumber(metric.getSourceCode().getSourceCode()));

        getMetricsCounts(metric, statistics);

        statistics.setEntropy(getEntropy(statistics));

        metric.setStatistics(statistics);
    }

    private static void getMetricsCounts(Metric metric, Statistics statistics) {
        statistics.setPmdViolations(metric.getPmdMetrics().size());
        statistics.setCheckStyleViolations(metric.getCheckstyleMetrics().size());
        metric.getPmdMetrics().forEach(pmd -> {
            if(!statistics.getPmdClasses().containsKey(pmd.getName())) {
                statistics.getPmdClasses().put(pmd.getName(), 1.0);
            } else {
                statistics.getPmdClasses().put(pmd.getName(), 1.0 + statistics.getPmdClasses().get(pmd.getName()));
            }
        });
        metric.getPmdMetrics().forEach(pmd -> {
            if(!statistics.getPmdWeightedClasses().containsKey(pmd.getName())) {
                statistics.getPmdWeightedClasses().put(pmd.getName(), (double) pmd.getPriority());
            } else {
                statistics.getPmdWeightedClasses().put(pmd.getName(), pmd.getPriority() + statistics.getPmdWeightedClasses().get(pmd.getName()));
            }
        });
        metric.getCheckstyleMetrics().forEach(checkStyle -> {
            if(!statistics.getCheckStyleClasses().containsKey(checkStyle.getName())) {
                statistics.getCheckStyleClasses().put(checkStyle.getName(), 1.0);
            } else {
                statistics.getCheckStyleClasses().put(checkStyle.getName(), 1.0 + statistics.getCheckStyleClasses().get(checkStyle.getName()));
            }
        });
    }

    private static double getEntropy(Statistics statistics) {
        final Map<String, Double> count = new HashMap<>();
        statistics.getPmdWeightedClasses().forEach((key, value) -> {
            count.put(key, Math.log(value + .1));
        });
        statistics.getCheckStyleClasses().forEach((key, value) -> {
            count.put(key, Math.log(value + .1));
        });
        double total = count.values().stream().mapToDouble(Double::doubleValue).sum();
        List<Double> probabilities = count.values().stream().map(item -> item / total).collect(Collectors.toList());
        double entropy = 0.0;
        for(double probability : probabilities) {
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
            if(className.contains(PUBLIC)) className = className.replace(PUBLIC, "");
            if(className.contains(PRIVATE)) className = className.replace(PRIVATE, "");
            if(className.contains(EXTENDS)) className = className.replace(className.substring(className.indexOf(EXTENDS)), "");
            if(className.contains(IMPLEMENTS)) className = className.replace(className.substring(className.indexOf(IMPLEMENTS)), "");
            className = className.trim();
            break;
        }
        return className;

    }

}
