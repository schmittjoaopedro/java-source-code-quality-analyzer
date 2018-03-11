package com.github.schmittjoaopedro.pmd;

import com.github.schmittjoaopedro.metrics.PMDMetrics;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PMDAnalyser {

    public PMDMetrics analyse(String sourceCode) {
        try {
            LanguageVersion languageVersion = LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion();
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
            return null;
        } catch (Exception e) {

        }
        return null;
    }

}
