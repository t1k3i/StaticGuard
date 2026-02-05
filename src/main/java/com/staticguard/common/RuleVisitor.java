package com.staticguard.common;

public interface RuleVisitor<T> {
    void run(T astRoot, RuleContext context);
}
