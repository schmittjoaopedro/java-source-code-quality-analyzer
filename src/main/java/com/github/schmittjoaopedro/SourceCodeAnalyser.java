package com.github.schmittjoaopedro;

import com.github.schmittjoaopedro.metrics.Metrics;
import com.github.schmittjoaopedro.pmd.PMDAnalyser;
import com.github.schmittjoaopedro.spotbugs.SpotBugsAnalyser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SourceCodeAnalyser  {

    private static Logger logger = LogManager.getLogger(SourceCodeAnalyser.class);

    public Metrics analyse(String sourceCode) {
        logger.info("Initializing source code analyse!");
        Metrics metrics = new Metrics();

        PMDAnalyser pmdAnalyser = new PMDAnalyser();
        metrics.setPmdMetrics(pmdAnalyser.analyse(sourceCode));

        SpotBugsAnalyser spotBugsAnalyser = new SpotBugsAnalyser();
        metrics.setSpotBugsMetrics(spotBugsAnalyser.analyse(sourceCode));

        return metrics;
    }

}
