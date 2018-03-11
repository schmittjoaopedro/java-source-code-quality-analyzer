package com.github.schmittjoaopedro.analyser;

import com.github.schmittjoaopedro.analyser.checkstyle.CheckstyleAnalyser;
import com.github.schmittjoaopedro.model.Metric;
import com.github.schmittjoaopedro.analyser.pmd.PMDAnalyser;
import com.github.schmittjoaopedro.analyser.spotbugs.SpotBugsAnalyser;
import com.github.schmittjoaopedro.mcc.utils.MccUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class SourceCodeAnalyser  {

    private static Logger logger = LogManager.getLogger(SourceCodeAnalyser.class);

    public Metric analyse(String sourceCode) {
        Metric metric = new Metric();
        metric.setClassName(MccUtils.extractClassNameFromSourceCode(sourceCode));
        CheckstyleAnalyser checkstyleAnalyser = new CheckstyleAnalyser();
        metric.setCheckstyleMetrics(checkstyleAnalyser.analyse(sourceCode));
        PMDAnalyser pmdAnalyser = new PMDAnalyser();
        metric.setPmdMetrics(pmdAnalyser.analyse(sourceCode));
        SpotBugsAnalyser spotBugsAnalyser = new SpotBugsAnalyser();
        metric.setSpotBugsMetrics(spotBugsAnalyser.analyse(sourceCode));
        MetricCalculator.calculate(metric);
        return metric;
    }

}
