package com.github.schmittjoaopedro.analyser;

import com.github.schmittjoaopedro.analyser.checkstyle.CheckstyleAnalyser;
import com.github.schmittjoaopedro.analyser.complexity.CyclomaticComplexityAnalyser;
import com.github.schmittjoaopedro.analyser.pmd.PMDAnalyser;
import com.github.schmittjoaopedro.model.Metric;
import com.github.schmittjoaopedro.model.SourceCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class SourceCodeAnalyser {

    private static Logger logger = LogManager.getLogger(SourceCodeAnalyser.class);

    public Metric analyse(SourceCode sourceCode) {
        Metric metric = new Metric();
        metric.setSourceCode(sourceCode);
        analyse(metric);
        return metric;
    }

    public void analyse(Metric metric) {
        SourceCode sourceCode = metric.getSourceCode();
        if (sourceCode != null && !StringUtils.isBlank(metric.getSource())) {
            try {
                sourceCode.setClassName(MetricCalculator.extractClassNameFromSourceCode(metric.getSource()));
                if (metric.isCheckStyle()) {
                    checkStyle(metric, sourceCode);
                }
                if (metric.isPmd()) {
                    checkPmd(metric, sourceCode);
                }
                if (metric.isCyclomaticComplexity()) {
                    cyclomaticComplexity(metric, sourceCode);
                }
                MetricCalculator.calculate(metric);
            } catch (Exception e) {
                logger.error(e.getMessage());
                logger.error("Error on load " + sourceCode.getClassName());
                throw new RuntimeException(e);
            }
        } else {
            metric.setError("Empty source code!");
        }
    }

    private void cyclomaticComplexity(Metric metric, SourceCode sourceCode) {
        try {
            CyclomaticComplexityAnalyser cyclomaticComplexityAnalyser = new CyclomaticComplexityAnalyser();
            metric.setCyclomaticComplexities(cyclomaticComplexityAnalyser.analyse(metric.getSource()));
        } catch (Exception e) {
            logger.error(e.getMessage());
            metric.setError(metric.getError() + "\nCyclomatic complexity error:" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void checkPmd(Metric metric, SourceCode sourceCode) {
        try {
            PMDAnalyser pmdAnalyser = new PMDAnalyser();
            metric.setPmdMetrics(pmdAnalyser.analyse(metric.getSource()));
        } catch (Exception e) {
            logger.error(e.getMessage());
            metric.setError(metric.getError() + "\nPMD error:" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void checkStyle(Metric metric, SourceCode sourceCode) {
        try {
            CheckstyleAnalyser checkstyleAnalyser = new CheckstyleAnalyser();
            metric.setCheckstyleMetrics(checkstyleAnalyser.analyse(metric.getSource()));
        } catch (Exception e) {
            logger.error(e.getMessage());
            metric.setError(metric.getError() + "\nCheckStyle error:" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
