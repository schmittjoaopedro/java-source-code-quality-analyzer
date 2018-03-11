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
import java.util.Collection;
import java.util.List;

public class SourceCodeAnalyserTest {

    private static Logger logger = LogManager.getLogger(SourceCodeAnalyserTest.class);

    @Test
    public void testCases() throws Exception {
        File[] files = new File(Paths.get("src", "test", "resources").toString()).listFiles();
        for(File file : files) {
            analyseSourceCode(file.getName());
        }
    }


    private void analyseSourceCode(String classFile) throws Exception {
        Path resourceDirectory = Paths.get("src", "test", "resources");
        Path sourcePath = Paths.get(resourceDirectory.toString(), classFile);
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
    }
}
