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

    @Test
    public void testComplexityErrorProblems() throws Exception {
        String javaSource1 = "package test;\n" +
                "\n" +
                "public class Test {\n" +
                "\tpublic int count(int a, int b, int c, int d, int e, int f, int g, int h, int i, int j, int k, int l) {\n" +
                "\t\tif(a < b) {\n" +
                "\t\t\treturn a;\n" +
                "\t\t} else if (b < c) {\n" +
                "\t\t\treturn b;\n" +
                "\t\t} else if (c < d) {\n" +
                "\t\t\treturn c;\n" +
                "\t\t} else if (d < e) {\n" +
                "\t\t\treturn d;\n" +
                "\t\t} else if (e < f) {\n" +
                "\t\t\treturn e;\n" +
                "\t\t} else if (f < g) {\n" +
                "\t\t\treturn f;\n" +
                "\t\t} else if (g < h) {\n" +
                "\t\t\treturn g;\n" +
                "\t\t} else if (h < i) {\n" +
                "\t\t\treturn h;\n" +
                "\t\t} else if (i < j) {\n" +
                "\t\t\treturn i;\n" +
                "\t\t} else if (j < k) {\n" +
                "\t\t\treturn j;\n" +
                "\t\t} else if (k < l) {\n" +
                "\t\t\treturn k;\n" +
                "\t\t} else {\n" +
                "\t\t\treturn l;\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";

        String javaSource2 = "package test;\n" +
                "\n" +
                "public class Test {\n" +
                "\tpublic int count(int a, int b, int c, int d, int e, int f, int g, int h, int i, int j, int k, int l) {\n" +
                "\t\tif(a < b) {\n" +
                "\t\t\treturn a;\n" +
                "\t\t} else if (b < c) {\n" +
                "\t\t\treturn b;\n" +
                "\t\t} else if (c < d) {\n" +
                "\t\t\treturn c;\n" +
                "\t\t} else if (d < e) {\n" +
                "\t\t\treturn d;\n" +
                "\t\t} else if (e < f) {\n" +
                "\t\t\treturn e;\n" +
                "\t\t} else if (f < g) {\n" +
                "\t\t\treturn f;\n" +
                "\t\t} else if (g < h) {\n" +
                "\t\t\treturn g;\n" +
                "\t\t} else if (h < i) {\n" +
                "\t\t\treturn h;\n" +
                "\t\t} else if (i < j) {\n" +
                "\t\t\treturn i;\n" +
                "\t\t} else if (j < k) {\n" +
                "\t\t\treturn j;\n" +
                "\t\t} else {\n" +
                "\t\t\treturn l;\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";

        CyclomaticComplexityAnalyser cyclomaticComplexityAnalyser = new CyclomaticComplexityAnalyser();
        List<CyclomaticComplexity> results1 = cyclomaticComplexityAnalyser.analyse(javaSource1);
        Assert.assertEquals(results1.get(0).getCyclomatic(), 12);

        List<CyclomaticComplexity> results2 = cyclomaticComplexityAnalyser.analyse(javaSource2);
        Assert.assertEquals(results2.get(0).getCyclomatic(), 11);

        PMDAnalyser pmdAnalyser = new PMDAnalyser();
        List<PMDMetric> resultsAnalyser1 = pmdAnalyser.analyse(javaSource1);
        Assert.assertEquals(resultsAnalyser1.size(), 46);
        for(PMDMetric violation : resultsAnalyser1) {
            System.out.println(violation.getName() + "," + violation.getBeginLine() + " = " + violation.getDescription());
            if(violation.getName().equals("ModifiedCyclomaticComplexity") && violation.getBeginLine() == 3) {
                Assert.assertEquals(violation.getDescription(), "The class 'Test' has a Modified Cyclomatic Complexity of 13 (Highest = 12).");
            }
            if(violation.getName().equals("StdCyclomaticComplexity") && violation.getBeginLine() == 3) {
                Assert.assertEquals(violation.getDescription(), "The class 'Test' has a Standard Cyclomatic Complexity of 13 (Highest = 12).");
            }
            if(violation.getName().equals("ModifiedCyclomaticComplexity") && violation.getBeginLine() == 4) {
                Assert.assertEquals(violation.getDescription(), "The method 'count' has a Modified Cyclomatic Complexity of 12.");
            }
            if(violation.getName().equals("StdCyclomaticComplexity") && violation.getBeginLine() == 4) {
                Assert.assertEquals(violation.getDescription(), "The method 'count' has a Standard Cyclomatic Complexity of 12.");
            }
            if(violation.getName().equals("CyclomaticComplexity") && violation.getBeginLine() == 4) {
                Assert.assertEquals(violation.getDescription(), "The method 'count(int, int, int, int, int, int, int, int, int, int, int, int)' has a cyclomatic complexity of 12.");
            }
        }

        List<PMDMetric> resultsAnalyser2 = pmdAnalyser.analyse(javaSource2);
        Assert.assertEquals(resultsAnalyser2.size(), 45);
        for(PMDMetric violation : resultsAnalyser2) {
            System.out.println(violation.getName() + "," + violation.getBeginLine() + " = " + violation.getDescription());
            if(violation.getName().equals("ModifiedCyclomaticComplexity") && violation.getBeginLine() == 3) {
                Assert.assertEquals(violation.getDescription(), "The class 'Test' has a Modified Cyclomatic Complexity of 12 (Highest = 11).");
            }
            if(violation.getName().equals("StdCyclomaticComplexity") && violation.getBeginLine() == 3) {
                Assert.assertEquals(violation.getDescription(), "The class 'Test' has a Standard Cyclomatic Complexity of 12 (Highest = 11).");
            }
            if(violation.getName().equals("ModifiedCyclomaticComplexity") && violation.getBeginLine() == 4) {
                Assert.assertEquals(violation.getDescription(), "The method 'count' has a Modified Cyclomatic Complexity of 11.");
            }
            if(violation.getName().equals("StdCyclomaticComplexity") && violation.getBeginLine() == 4) {
                Assert.assertEquals(violation.getDescription(), "The method 'count' has a Standard Cyclomatic Complexity of 11.");
            }
            if(violation.getName().equals("CyclomaticComplexity") && violation.getBeginLine() == 4) {
                Assert.assertEquals(violation.getDescription(), "The method 'count(int, int, int, int, int, int, int, int, int, int, int, int)' has a cyclomatic complexity of 11.");
            }
        }
    }
}
