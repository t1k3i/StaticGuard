package com.staticguard.parser.language;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileNotFoundException;

public class JavaLanguageParser {

    private final File file;

    public JavaLanguageParser(File file) {
        this.file = file;
    }

    public CompilationUnit parse() throws FileNotFoundException {
        return StaticJavaParser.parse(file);
    }
}
