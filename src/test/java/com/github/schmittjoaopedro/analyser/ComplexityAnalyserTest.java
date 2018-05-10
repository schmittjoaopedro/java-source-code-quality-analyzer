package com.github.schmittjoaopedro.analyser;

import com.github.schmittjoaopedro.analyser.complexity.CyclomaticComplexityAnalyser;
import com.github.schmittjoaopedro.analyser.pmd.PMDAnalyser;
import com.github.schmittjoaopedro.model.CyclomaticComplexity;
import com.github.schmittjoaopedro.model.PMDMetric;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ComplexityAnalyserTest {

    @Test
    public void testComplexityNonCache() throws Exception {

        String javaSource1 = "package test;\n" +
                "\n" +
                "public class Test {\n" +
                "\tpublic int count(int a, int b, int c) {\n" +
                "\t\tif(a < b) {\n" +
                "\t\t\treturn a;\n" +
                "\t\t} else if (b < c) {\n" +
                "\t\t\treturn b;\n" +
                "\t\t} else {\n" +
                "\t\t\treturn c;\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";

        String javaSource2 = "package test;\n" +
                "\n" +
                "public class Test {\n" +
                "\tpublic int count(int a, int b, int c) {\n" +
                "\t\tif(b < c) {\n" +
                "\t\t\treturn c;\n" +
                "\t\t} else {\n" +
                "\t\t\treturn b;\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";

        CyclomaticComplexityAnalyser cyclomaticComplexityAnalyser = new CyclomaticComplexityAnalyser();
        List<CyclomaticComplexity> results1 = cyclomaticComplexityAnalyser.analyse(javaSource1);
        Assert.assertEquals(results1.get(0).getCyclomatic(), 3);

        List<CyclomaticComplexity> results2 = cyclomaticComplexityAnalyser.analyse(javaSource2);
        Assert.assertEquals(results2.get(0).getCyclomatic(), 2);

        PMDAnalyser pmdAnalyser = new PMDAnalyser();
        List<PMDMetric> resultsAnalyser1 = pmdAnalyser.analyse(javaSource1);
        Assert.assertEquals(resultsAnalyser1.size(), 12);
        for(PMDMetric violation : resultsAnalyser1) {
            System.out.println(violation.getDescription());
        }

        List<PMDMetric> resultsAnalyser2 = pmdAnalyser.analyse(javaSource2);
        Assert.assertEquals(resultsAnalyser2.size(), 11);
        for(PMDMetric violation : resultsAnalyser2) {
            System.out.println(violation.getDescription());
        }
    }

}
