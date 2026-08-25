package com.staticguard.parser;

import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;

import java.io.File;
import java.io.IOException;

public abstract class LanguageParser<T> {
    protected final File file;

    protected LanguageParser(File file) {
        this.file = file;
    }

    public abstract T parse() throws IOException;

    public abstract void handle(
            CLIOptionsConfig config,
            RuleContext context,
            ProjectContext projectContext
    ) throws Exception;
}
