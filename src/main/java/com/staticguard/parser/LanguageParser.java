package com.staticguard.parser;

import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.Language;

import java.io.File;
import java.io.IOException;

public abstract class LanguageParser<T> {
    protected final File file;
    protected final Language language;

    protected LanguageParser(File file, Language language) {
        this.file = file;
        this.language = language;
    }

    public abstract T parse() throws IOException;

    public abstract void handle(
            CLIOptionsConfig config,
            RuleContext context,
            ProjectContext projectContext
    ) throws Exception;

    public Language getLanguage() {
        return language;
    }
}
