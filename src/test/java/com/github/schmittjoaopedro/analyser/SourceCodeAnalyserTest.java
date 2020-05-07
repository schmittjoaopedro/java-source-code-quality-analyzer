package com.github.schmittjoaopedro.analyser;

import com.github.schmittjoaopedro.model.Metric;
import com.github.schmittjoaopedro.model.SourceCode;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
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
        Collections.sort(metrics, (a, b) -> {
            if (a.getStatistics().getEntropy() < b.getStatistics().getEntropy()) {
                return -1;
            } else if (a.getStatistics().getEntropy() < b.getStatistics().getEntropy()) {
                return 1;
            } else {
                return 0;
            }
        });
        for (Metric m : metrics) {
            System.out.println(m.getSourceCode().getClassName() + "," + m.getStatistics().getEntropy());
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
        Collections.sort(metrics, (a, b) -> {
            if (a.getStatistics().getEntropy() < b.getStatistics().getEntropy()) {
                return -1;
            } else if (a.getStatistics().getEntropy() < b.getStatistics().getEntropy()) {
                return 1;
            } else {
                return 0;
            }
        });
        for (Metric m : metrics) {
            System.out.println(m.getSourceCode().getClassName() + "," + m.getStatistics().getEntropy());
        }
    }


    private Metric analyseSourceCode(Path rootPath, String classFile) throws Exception {
        Path sourcePath = Paths.get(rootPath.toString(), classFile);
        String sourceCode = FileUtils.readFileToString(new File(sourcePath.toString()), "UTF-8");
        Metric metric = new Metric();
        metric.setSourceCode(new SourceCode());
        metric.getSourceCode().setSourceCode(sourceCode);
        metric.setPmd(true);
        metric.setCheckStyle(true);
        SourceCodeAnalyser sourceCodeAnalyser = new SourceCodeAnalyser();
        sourceCodeAnalyser.analyse(metric);
        return metric;
    }
}
