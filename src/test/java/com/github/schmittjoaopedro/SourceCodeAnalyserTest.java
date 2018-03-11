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

public class SourceCodeAnalyserTest {

    private static Logger logger = LogManager.getLogger(SourceCodeAnalyserTest.class);

    @Test
    public void testCases() throws Exception {
        analyseSourceCode("TestClass1");
        //analyseSourceCode("ArrayEquality");
        //analyseSourceCode("EASYO31231231231");
    }


    private void analyseSourceCode(String classFile) throws Exception {
        Path resourceDirectory = Paths.get("src", "test", "resources");
        Path sourcePath = Paths.get(resourceDirectory.toString(), classFile + ".java");
        String sourceCode = FileUtils.readFileToString(new File(sourcePath.toString()), "UTF-8");
        SourceCodeAnalyser sourceCodeAnalyser = new SourceCodeAnalyser();
        Metrics metrics = sourceCodeAnalyser.analyse(sourceCode);
        logger.info("------------------ " + classFile + " ------------------");
        metrics.getPmdMetrics().forEach(pmd -> {
            logger.info("PMD: (" + pmd.getBeginLine() + ") " + pmd.getDescription());
        });
        metrics.getSpotBugsMetrics().forEach(spotBugs -> {
            logger.info("SpotBugs: " + spotBugs.getMessage());
        });
    }
}
