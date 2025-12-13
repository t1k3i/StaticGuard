package com.staticguard.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.enums.Language;

import java.io.File;
import java.io.FileNotFoundException;

public class JavaLanguageParser extends LanguageParser<CompilationUnit> {

    public JavaLanguageParser(File file) {
        super(file, Language.JAVA);
    }

    @Override
    public CompilationUnit parse() throws FileNotFoundException {
        return StaticJavaParser.parse(file);
    }
}
