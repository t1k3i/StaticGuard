package com.staticguard.handlers;

import com.staticguard.cli.CLIOptionsConfig;

import java.io.File;

public interface LanguageHandler {
    void handle(Object root, CLIOptionsConfig options, File sourceFile);
}
