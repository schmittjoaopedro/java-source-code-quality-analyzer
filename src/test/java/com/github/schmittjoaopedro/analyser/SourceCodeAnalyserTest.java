package com.github.schmittjoaopedro.analyser;

import com.github.schmittjoaopedro.model.Metric;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
        List<Metric> metrics = new ArrayList<>();
        for (File file : files) {
            metrics.add(analyseSourceCode(rootPath, file.getName()));
        }
        Collections.sort(metrics, new Comparator<Metric>() {
            @Override
            public int compare(Metric a, Metric b) {
                if (a.getClassComplexity() < b.getClassComplexity()) {
                    return -1;
                } else if (a.getClassComplexity() < b.getClassComplexity()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        for(Metric m : metrics) {
            logger.info(m.getClassName() + " = " + m.getClassComplexity());
        }
    }

    @Test
    public void testProject1Cases() throws Exception {
        Path rootPath = Paths.get("src", "test", "resources", "project1");
        File[] files = new File(rootPath.toString()).listFiles();
        List<Metric> metrics = new ArrayList<>();
        for (File file : files) {
            metrics.add(analyseSourceCode(rootPath, file.getName()));
        }
        Collections.sort(metrics, new Comparator<Metric>() {
            @Override
            public int compare(Metric a, Metric b) {
                if (a.getClassComplexity() < b.getClassComplexity()) {
                    return -1;
                } else if (a.getClassComplexity() < b.getClassComplexity()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        for(Metric m : metrics) {
            logger.info(m.getClassName() + " = " + m.getClassComplexity());
        }
    }


    private Metric analyseSourceCode(Path rootPath, String classFile) throws Exception {
        Path sourcePath = Paths.get(rootPath.toString(), classFile);
        String sourceCode = FileUtils.readFileToString(new File(sourcePath.toString()), "UTF-8");
        SourceCodeAnalyser sourceCodeAnalyser = new SourceCodeAnalyser();
        Metric metric = sourceCodeAnalyser.analyse(sourceCode);
        logger.info("------------------ " + classFile + " ------------------");
        metric.getCheckstyleMetrics().forEach(checkStyle -> {
            //logger.info("CheckStyle: [" + checkStyle.getSeverityLevel() + "](" + checkStyle.getLine() + "): " + checkStyle.getDescription());
        });
        metric.getPmdMetrics().forEach(pmd -> {
            //logger.info("PMD: [" + pmd.getPriority() + "](" + pmd.getBeginLine() + ") " + pmd.getDescription());
        });
        metric.getSpotBugsMetrics().forEach(spotBugs -> {
            //logger.info("SpotBugs [" + spotBugs.getPriority() + "]: " + spotBugs.getMessage());
        });
        logger.info("Complexity CheckStyle: " + metric.getCheckStyleComplexity());
        logger.info("Complexity PMD: " + metric.getPmdComplexity());
        logger.info("Complexity SpotBugs: " + metric.getSpotBugsComplexity());
        logger.info("Complexity Class: " + metric.getClassComplexity());
        return metric;
    }
}
