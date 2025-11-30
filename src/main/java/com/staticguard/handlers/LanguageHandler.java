package com.staticguard.handlers;

import com.staticguard.cli.CLIOptionsConfig;

public interface LanguageHandler {
    void handle(Object root, CLIOptionsConfig options);
}
