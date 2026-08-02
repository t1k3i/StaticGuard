package com.staticguard.rules;

import com.staticguard.common.RuleContext;

public interface RuleVisitor<T> {
    void run(T astRoot, RuleContext context);
}
