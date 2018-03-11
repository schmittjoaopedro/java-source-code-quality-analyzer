package com.github.schmittjoaopedro.analyser;

import com.github.schmittjoaopedro.analyser.checkstyle.CheckstyleAnalyser;
import com.github.schmittjoaopedro.model.Metric;
import com.github.schmittjoaopedro.analyser.pmd.PMDAnalyser;
import com.github.schmittjoaopedro.analyser.spotbugs.SpotBugsAnalyser;
import com.github.schmittjoaopedro.mcc.utils.MccUtils;
import com.github.schmittjoaopedro.model.SourceCode;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class SourceCodeAnalyser  {

    private static Logger logger = LogManager.getLogger(SourceCodeAnalyser.class);

    public Metric analyse(String sourceCode) {
        Metric metric = new Metric();
        try {
            metric.setClassName(MccUtils.extractClassNameFromSourceCode(sourceCode));
            CheckstyleAnalyser checkstyleAnalyser = new CheckstyleAnalyser();
            metric.setCheckstyleMetrics(checkstyleAnalyser.analyse(sourceCode));
            PMDAnalyser pmdAnalyser = new PMDAnalyser();
            metric.setPmdMetrics(pmdAnalyser.analyse(sourceCode));
            SpotBugsAnalyser spotBugsAnalyser = new SpotBugsAnalyser();
            metric.setSpotBugsMetrics(spotBugsAnalyser.analyse(sourceCode));
            MetricCalculator.calculate(metric);
        } catch (Exception e) {
            metric.setError(e.getMessage());
        }
        return metric;
    }

    public Metric analyse(SourceCode sourceCode) {
        Metric metric = new Metric();
        if (!StringUtils.isBlank(sourceCode.getSourceCode())) {
            try {
                metric.setClassName(MccUtils.extractClassNameFromSourceCode(sourceCode.getSourceCode()));
                if(sourceCode.isCheckStyle()) {
                    checkStyle(metric, sourceCode);
                }
                if(sourceCode.isPmd()) {
                    checkPmd(metric, sourceCode);
                }
                if(sourceCode.isSpotBugs()) {
                    checkSpotBugs(metric, sourceCode);
                }
                MetricCalculator.calculate(metric);
            } catch (Exception e) {
                metric.setError(e.getMessage());
            }
        } else {
            metric.setError("Empty source code!");
        }

        return metric;
    }

    private void checkPmd(Metric metric, SourceCode sourceCode) {
        try {
            PMDAnalyser pmdAnalyser = new PMDAnalyser();
            metric.setPmdMetrics(pmdAnalyser.analyse(sourceCode.getSourceCode()));
        } catch (Exception e) {
            metric.setError(metric.getError() + "\nPMD error:" + e.getMessage());
        }
    }

    private void checkStyle(Metric metric, SourceCode sourceCode) {
        try {
            CheckstyleAnalyser checkstyleAnalyser = new CheckstyleAnalyser();
            metric.setCheckstyleMetrics(checkstyleAnalyser.analyse(sourceCode.getSourceCode()));
        } catch (Exception e) {
            metric.setError(metric.getError() + "\nCheckStyle error:" + e.getMessage());
        }
    }

    private void checkSpotBugs(Metric metric, SourceCode sourceCode) {
        try {
            SpotBugsAnalyser spotBugsAnalyser = new SpotBugsAnalyser();
            metric.setSpotBugsMetrics(spotBugsAnalyser.analyse(sourceCode.getSourceCode()));
        } catch (Exception e) {
            metric.setError(metric.getError() + "\nSpotBug error:" + e.getMessage());
        }
    }

}