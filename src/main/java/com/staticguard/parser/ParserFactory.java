package com.staticguard.parser;

import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.enums.Language;

import java.io.File;

public class ParserFactory {

    public static LanguageParser<?> createParser(File file, CLIOptionsConfig config) {
        return switch (config.getLang()) {
            case Language.JAVA -> new JavaLanguageParser(file);
            case Language.C -> new CLanguageParser(file);
        };
    }

    public static LanguageParser<?> createParser(
            File file,
            File sourceRoot,
            CLIOptionsConfig config
    ) {
        return switch (config.getLang()) {
            case Language.JAVA -> new JavaLanguageParser(file, sourceRoot);
            case Language.C -> new CLanguageParser(file);
        };
    }

    // This is only for tests
    public static LanguageParser<?> createParser(File file) {
        String ext = getExtension(file);

        return switch (ext) {
            case "c", "h" -> new CLanguageParser(file);
            case "java" -> new JavaLanguageParser(file);
            default -> throw new IllegalArgumentException("Unsupported file type: " + ext);
        };
    }

    private static String getExtension(File file) {
        String name = file.getName();
        int idx = name.lastIndexOf('.');
        return idx == -1 ? "" : name.substring(idx + 1).toLowerCase();
    }
}
