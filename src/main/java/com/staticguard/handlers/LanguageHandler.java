package com.staticguard.handlers;

import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;

public interface LanguageHandler<T> {
    void handle(T root, CLIOptionsConfig options, RuleContext context, ProjectContext projectContext);
}
