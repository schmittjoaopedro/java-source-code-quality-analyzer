package com.github.schmittjoaopedro.analyser;

import com.github.schmittjoaopedro.model.CheckstyleMetric;
import com.github.schmittjoaopedro.model.Metric;
import com.github.schmittjoaopedro.model.PMDMetric;
import com.github.schmittjoaopedro.model.SpotBugsMetric;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        metric.setLinesNumber(getLinesNumber(metric.getSourceCode()));
        metric.setNotEmptyLinesNumber(getNotEmptyLinesNumber(metric.getSourceCode()));
        metric.setUncommentedLinesNumber(getUncommentedLinesNumber(metric.getSourceCode()));
        metric.setNormalizedLinesNumber(getNormalizedLinesNumber(metric.getSourceCode()));
        metric.setComplexityFactor(metric.getClassComplexity() / Math.max(1.0, metric.getNormalizedLinesNumber())); // Evict division by zero
    }

    private static long getLinesNumber(String sourceCode) {
        return Arrays.stream(sourceCode.split("\n")).count();
    }

    private static long getNotEmptyLinesNumber(String sourceCode) {
        return Arrays.stream(sourceCode.split("\n")).map(item -> item.replace("\r", StringUtils.EMPTY).trim()).filter(item -> !item.isEmpty()).count();
    }

    private static long getUncommentedLinesNumber(String sourceCode) {
        //String sourceCodeFiltered = sourceCode.replaceAll("/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/", StringUtils.EMPTY);
        //sourceCodeFiltered = sourceCodeFiltered.replaceAll("[^:]\\/\\/.*", StringUtils.EMPTY);
        String sourceCodeFiltered = sourceCode.replaceAll("((['\"])(?:(?!\\2|\\\\).|\\\\.)*\\2)|\\/\\/[^\\n]*|\\/\\*(?:[^*]|\\*(?!\\/))*\\*\\/", StringUtils.EMPTY);
        return getLinesNumber(sourceCodeFiltered);
    }

    private static long getNormalizedLinesNumber(String sourceCode) {
        //String sourceCodeFiltered = sourceCode.replaceAll("/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/", StringUtils.EMPTY);
        //sourceCodeFiltered = sourceCodeFiltered.replaceAll("[^:]\\/\\/.*", StringUtils.EMPTY);
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
