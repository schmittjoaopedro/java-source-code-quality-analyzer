package com.github.schmittjoaopedro;

import com.github.schmittjoaopedro.metrics.Metrics;
import com.github.schmittjoaopedro.spotbugs.SpotBugsAnalyser;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SourceCodeAnalyserTest {

    @Test
    public void test1() throws Exception {
        Path resourceDirectory = Paths.get("src", "test", "resources");
        Path sourcePath = Paths.get(resourceDirectory.toString(), "TestClass1.java");
        String sourceCode = FileUtils.readFileToString(new File(sourcePath.toString()), "UTF-8");
        SourceCodeAnalyser sourceCodeAnalyser = new SourceCodeAnalyser();
        Metrics metrics = sourceCodeAnalyser.analyse(sourceCode);
        Assertions.assertThat(metrics).isNotNull();
    }

    @Test
    public void test2() throws Exception {
        Path resourceDirectory = Paths.get("src", "test", "resources");
        Path sourcePath = Paths.get(resourceDirectory.toString(), "ArrayEquality.java");
        String sourceCode = FileUtils.readFileToString(new File(sourcePath.toString()), "UTF-8");
        SourceCodeAnalyser sourceCodeAnalyser = new SourceCodeAnalyser();
        Metrics metrics = sourceCodeAnalyser.analyse(sourceCode);
        Assertions.assertThat(metrics).isNotNull();
    }

}
