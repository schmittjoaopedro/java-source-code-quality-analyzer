package com.github.schmittjoaopedro.analyser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.github.schmittjoaopedro.analyser.checkstyle.CheckstyleAnalyser;
import com.github.schmittjoaopedro.analyser.pmd.PMDAnalyser;
import com.github.schmittjoaopedro.model.Metric;
import com.github.schmittjoaopedro.model.SourceCode;

@Service
public class SourceCodeAnalyser {

    private static Logger logger = LogManager.getLogger(SourceCodeAnalyser.class);

    public Metric analyse(String sourceCode) {
        Metric metric = new Metric();
        /*try {
            metric.setClassName(MccUtils.extractClassNameFromSourceCode(sourceCode));
            CheckstyleAnalyser checkstyleAnalyser = new CheckstyleAnalyser();
            metric.setCheckstyleMetrics(checkstyleAnalyser.analyse(sourceCode));
            PMDAnalyser pmdAnalyser = new PMDAnalyser();
            metric.setPmdMetrics(pmdAnalyser.analyse(sourceCode));
            SpotBugsAnalyser spotBugsAnalyser = new SpotBugsAnalyser();
            metric.setSpotBugsMetrics(spotBugsAnalyser.analyse(sourceCode));
            MetricCalculator.calculate(metric);
        } catch (Exception e) {
            logger.error(e);
            metric.setError(e.getMessage());
        }*/
        return metric;
    }

    public Metric analyse(SourceCode sourceCode) {
        Metric metric = new Metric();
        analyse(sourceCode, metric);
        return metric;
    }

    public void analyse(SourceCode sourceCode, Metric metric) {
        if (sourceCode.getSourceCode() != null) {
            try {
                metric.setClassName(MetricCalculator.extractClassNameFromSourceCode(sourceCode.getSourceCode()));
                if (sourceCode.isCheckStyle()) {
                    checkStyle(metric, sourceCode);
                }
                if (sourceCode.isPmd()) {
                    checkPmd(metric, sourceCode);
                }
                if (sourceCode.isSpotBugs()) {
                    checkSpotBugs(metric, sourceCode);
                }
                MetricCalculator.calculate(metric);
            } catch (Exception e) {
                logger.error(e);
                metric.setError(e.getMessage());
            }
        } else {
            metric.setError("Empty source code!");
        }
    }

    private void checkPmd(Metric metric, SourceCode sourceCode) {
        try {
            PMDAnalyser pmdAnalyser = new PMDAnalyser();
            metric.setPmdMetrics(pmdAnalyser.analyse(sourceCode.getSourceCode()));
        } catch (Exception e) {
            logger.error(e);
            metric.setError(metric.getError() + "\nPMD error:" + e.getMessage());
        }
    }

    private void checkStyle(Metric metric, SourceCode sourceCode) {
        try {
            CheckstyleAnalyser checkstyleAnalyser = new CheckstyleAnalyser();
            metric.setCheckstyleMetrics(checkstyleAnalyser.analyse(sourceCode.getSourceCode()));
        } catch (Exception e) {
            logger.error(e);
            metric.setError(metric.getError() + "\nCheckStyle error:" + e.getMessage());
        }
    }

    private void checkSpotBugs(Metric metric, SourceCode sourceCode) {
        try {
            //SpotBugsAnalyser spotBugsAnalyser = new SpotBugsAnalyser();
            //metric.setSpotBugsMetrics(spotBugsAnalyser.analyse(sourceCode.getSourceCode()));
        } catch (Exception e) {
            logger.error(e);
            metric.setError(metric.getError() + "\nSpotBug error:" + e.getMessage());
        }
    }

}
