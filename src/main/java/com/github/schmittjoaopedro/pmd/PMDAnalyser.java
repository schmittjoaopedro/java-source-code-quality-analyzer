package com.github.schmittjoaopedro.pmd;

import com.github.schmittjoaopedro.metrics.PMDMetrics;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.java.JavaLanguageHandler;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PMDAnalyser {

    private static Logger logger = LogManager.getLogger(PMDAnalyser.class);

    public List<PMDMetrics> analyse(String sourceCode) {
        try {
            //LanguageVersion languageVersion = new LanguageVersion("Java", "9", new JavaLanguageHandler(9), true);
            InputStream stream = new ByteArrayInputStream(sourceCode.getBytes(StandardCharsets.UTF_8));
            PMD pmd = new PMD();
            //pmd.getConfiguration().setDefaultLanguageVersion(languageVersion);
            RuleContext ctx = new RuleContext();
            Report report = new Report();
            ctx.setReport(report);
            ctx.setSourceCodeFilename("n/a");
            //ctx.setLanguageVersion(languageVersion);
            ctx.setIgnoreExceptions(false);
            RuleSets ruleSets = new RuleSets();
            new RuleSetFactory().getRegisteredRuleSets().forEachRemaining(ruleSet -> {
                ruleSets.addRuleSet(ruleSet);
            });
            pmd.getSourceCodeProcessor().processSourceCode(stream, ruleSets, ctx);
            List<PMDMetrics> metrics = new ArrayList<>();
            report.getViolationTree().iterator().forEachRemaining(violation -> {
                PMDMetrics pmdMetrics = new PMDMetrics();
                pmdMetrics.setBeginLine(violation.getBeginLine());
                pmdMetrics.setBeginColumn(violation.getBeginColumn());
                pmdMetrics.setEndLine(violation.getEndLine());
                pmdMetrics.setEndColumn(violation.getEndColumn());
                pmdMetrics.setDescription(violation.getDescription());
                pmdMetrics.setRule(violation.getRule().getDescription());
                pmdMetrics.setMessage(violation.getRule().getMessage());
                pmdMetrics.setPriority(violation.getRule().getPriority().getPriority());
                metrics.add(pmdMetrics);
            });
            return metrics;
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

}
