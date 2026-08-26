package com.staticguard.visitors.c;

import com.staticguard.common.RuleContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class CVisitorHelper {
    protected static void report(String message, ParserRuleContext ctx, RuleContext context) {
        int line = ctx != null && ctx.getStart() != null ? ctx.getStart().getLine() : -1;
        context.report(message, line);
    }
}
