package com.github.schmittjoaopedro.analyser.pmd;

import com.github.schmittjoaopedro.model.PMDMetric;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.lang.LanguageVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PMDAnalyser {

    private static Logger logger = LogManager.getLogger(PMDAnalyser.class);

    public List<PMDMetric> analyse(String sourceCode) throws Exception {
        try {
            LanguageVersion languageVersion = LanguageVersion.JAVA_18;
            InputStream stream = new ByteArrayInputStream(sourceCode.getBytes(StandardCharsets.UTF_8));
            PMD pmd = new PMD();
            pmd.getConfiguration().setDefaultLanguageVersion(languageVersion);
            RuleContext ctx = new RuleContext();
            Report report = new Report();
            ctx.setReport(report);
            ctx.setSourceCodeFilename("n/a");
            ctx.setLanguageVersion(languageVersion);
            ctx.setIgnoreExceptions(false);
            RuleSets ruleSets = new RuleSets();
            new RuleSetFactory().getRegisteredRuleSets().forEachRemaining(ruleSet -> {
                ruleSets.addRuleSet(ruleSet);
            });
            pmd.getSourceCodeProcessor().processSourceCode(stream, ruleSets, ctx);
            List<PMDMetric> metrics = new ArrayList<>();
            report.iterator().forEachRemaining(violation -> {
                PMDMetric pmdMetric = new PMDMetric();
                pmdMetric.setBeginLine(violation.getBeginLine());
                pmdMetric.setBeginColumn(violation.getBeginColumn());
                pmdMetric.setEndLine(violation.getEndLine());
                pmdMetric.setEndColumn(violation.getEndColumn());
                pmdMetric.setDescription(violation.getDescription());
                pmdMetric.setRule(violation.getRule().getDescription());
                pmdMetric.setMessage(violation.getRule().getMessage());
                pmdMetric.setPriority(violation.getRule().getPriority().getPriority());
                metrics.add(pmdMetric);
            });
            return metrics;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }

}
