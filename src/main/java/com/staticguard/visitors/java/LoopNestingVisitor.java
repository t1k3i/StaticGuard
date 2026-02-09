package com.staticguard.visitors.java;

import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.sql.SQLIntegrityConstraintViolationException;

public class LoopNestingVisitor extends VoidVisitorAdapter<Void> {
    private int currentDepth = 0;
    private Integer maxDepth;

    public LoopNestingVisitor(Integer maxDepth) {
        this.maxDepth = maxDepth;
    }

    private void enterLoop() {
        currentDepth++;
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }
    }

    private void exitLoop() {
        currentDepth--;
    }

    @Override
    public void visit(ForStmt n, Void arg) {
        enterLoop();
        super.visit(n, arg);
        exitLoop();
    }

    @Override
    public void visit(WhileStmt n, Void arg) {
        enterLoop();
        super.visit(n, arg);
        exitLoop();
    }

    @Override
    public void visit(DoStmt n, Void arg) {
        enterLoop();
        super.visit(n, arg);
        exitLoop();
    }

    @Override
    public void visit(ForEachStmt n, Void arg) {
        enterLoop();
        super.visit(n, arg);
        exitLoop();
    }
}
