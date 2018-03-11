package com.github.schmittjoaopedro;

import com.github.schmittjoaopedro.metrics.Metrics;
import com.github.schmittjoaopedro.spotbugs.SpotBugsAnalyser;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SourceCodeAnalyserTest {

    private static Logger logger = LogManager.getLogger(SourceCodeAnalyserTest.class);

    @Test
    public void testSampleCases() throws Exception {
        Path rootPath = Paths.get("src", "test", "resources", "samples");
        File[] files = new File(rootPath.toString()).listFiles();
        List<Metrics> metrics = new ArrayList<>();
        for (File file : files) {
            metrics.add(analyseSourceCode(rootPath, file.getName()));
        }
        Collections.sort(metrics, new Comparator<Metrics>() {
            @Override
            public int compare(Metrics a, Metrics b) {
                if (a.getClassComplexity() < b.getClassComplexity()) {
                    return -1;
                } else if (a.getClassComplexity() < b.getClassComplexity()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        for(Metrics m : metrics) {
            logger.info(m.getClassName() + " = " + m.getClassComplexity());
        }
    }

    @Test
    public void testProject1Cases() throws Exception {
        Path rootPath = Paths.get("src", "test", "resources", "project1");
        File[] files = new File(rootPath.toString()).listFiles();
        List<Metrics> metrics = new ArrayList<>();
        for (File file : files) {
            metrics.add(analyseSourceCode(rootPath, file.getName()));
        }
        Collections.sort(metrics, new Comparator<Metrics>() {
            @Override
            public int compare(Metrics a, Metrics b) {
                if (a.getClassComplexity() < b.getClassComplexity()) {
                    return -1;
                } else if (a.getClassComplexity() < b.getClassComplexity()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        for(Metrics m : metrics) {
            logger.info(m.getClassName() + " = " + m.getClassComplexity());
        }
    }


    private Metrics analyseSourceCode(Path rootPath, String classFile) throws Exception {
        Path sourcePath = Paths.get(rootPath.toString(), classFile);
        String sourceCode = FileUtils.readFileToString(new File(sourcePath.toString()), "UTF-8");
        SourceCodeAnalyser sourceCodeAnalyser = new SourceCodeAnalyser();
        Metrics metrics = sourceCodeAnalyser.analyse(sourceCode);
        logger.info("------------------ " + classFile + " ------------------");
        metrics.getCheckstyleMetrics().forEach(checkStyle -> {
            //logger.info("CheckStyle: [" + checkStyle.getSeverityLevel() + "](" + checkStyle.getLine() + "): " + checkStyle.getDescription());
        });
        metrics.getPmdMetrics().forEach(pmd -> {
            //logger.info("PMD: [" + pmd.getPriority() + "](" + pmd.getBeginLine() + ") " + pmd.getDescription());
        });
        metrics.getSpotBugsMetrics().forEach(spotBugs -> {
            //logger.info("SpotBugs [" + spotBugs.getPriority() + "]: " + spotBugs.getMessage());
        });
        logger.info("Complexity CheckStyle: " + metrics.getCheckStyleComplexity());
        logger.info("Complexity PMD: " + metrics.getPmdComplexity());
        logger.info("Complexity SpotBugs: " + metrics.getSpotBugsComplexity());
        logger.info("Complexity Class: " + metrics.getClassComplexity());
        return metrics;
    }
}
