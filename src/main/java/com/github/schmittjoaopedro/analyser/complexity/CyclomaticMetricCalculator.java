package com.github.schmittjoaopedro.analyser.complexity;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.metrics.AbstractMetricsComputer;

import java.util.ArrayList;
import java.util.List;

public class CyclomaticMetricCalculator extends AbstractMetricsComputer<ASTAnyTypeDeclaration, ASTMethodOrConstructorDeclaration> {

    @Override
    protected List<ASTMethodOrConstructorDeclaration> findOperations(ASTAnyTypeDeclaration node) {
        List<ASTMethodOrConstructorDeclaration> operations = new ArrayList<>();
        for (ASTAnyTypeBodyDeclaration decl : node.getDeclarations()) {
            if (decl.jjtGetNumChildren() > 0 && decl.jjtGetChild(0) instanceof ASTMethodOrConstructorDeclaration) {
                operations.add((ASTMethodOrConstructorDeclaration) decl.jjtGetChild(0));
            }
        }
        return operations;
    }
}
