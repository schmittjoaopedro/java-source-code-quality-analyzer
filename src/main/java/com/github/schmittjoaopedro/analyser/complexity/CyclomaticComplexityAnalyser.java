package com.github.schmittjoaopedro.analyser.complexity;

import com.github.schmittjoaopedro.model.CyclomaticComplexity;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.symboltable.ScopeAndDeclarationFinder;
import net.sourceforge.pmd.lang.metrics.BasicMetricMemoizer;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CyclomaticComplexityAnalyser {

    public List<CyclomaticComplexity> analyse(String sourceCode) throws Exception {
        clearCache();
        ASTCompilationUnit ast = parse(sourceCode);
        ASTClassOrInterfaceDeclaration clazz = ast.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class);
        List<CyclomaticComplexity> cyclomaticComplexities = new ArrayList<>();
        for (ASTAnyTypeBodyDeclaration declaration : clazz.getDeclarations()) {
            ASTMethodDeclaration method = declaration.getFirstDescendantOfType(ASTMethodDeclaration.class);
            if (method != null) {
                int cyclomaticValue = (int) new CyclomaticMetricCalculator().computeForOperation(JavaOperationMetricKey.CYCLO, method, false, MetricOptions.emptyOptions(), new BasicMetricMemoizer<>());
                cyclomaticComplexities.add(new CyclomaticComplexity(method.getMethodName(), cyclomaticValue));
            }
        }
        return cyclomaticComplexities;
    }

    private ASTCompilationUnit parse(final String code) {
        final LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion().getLanguageVersionHandler();
        final ParserOptions options = languageVersionHandler.getDefaultParserOptions();
        final ASTCompilationUnit ast = (ASTCompilationUnit) languageVersionHandler.getParser(options).parse(null, new StringReader(code));
        ast.jjtAccept(new ScopeAndDeclarationFinder(), null);
        return ast;
    }

    private static void clearCache() throws Exception {

    }

}
