package com.staticguard.parser;

import java.io.File;

public class ParserFactory {
    public static LanguageParser<?> createParser(File file) {
        if (file == null)
            throw new IllegalArgumentException("File cannot be null");

        if (!file.exists())
            throw new IllegalArgumentException("File does not exist: " + file.getAbsolutePath());

        String ext = getExtension(file);

        return switch (ext) {
            case "c", "h" -> new CLanguageParser(file);
            case "java" -> new JavaLanguageParser(file);
            default -> throw new IllegalArgumentException("Unsupported file type: " + ext);
        };
    }

    public static LanguageParser<?> createParser(
            File file,
            File sourceRoot
    ) {
        if (file == null)
            throw new IllegalArgumentException("File cannot be null");

        if (!file.exists())
            throw new IllegalArgumentException(
                    "File does not exist: " + file.getAbsolutePath()
            );

        String ext = getExtension(file);

        return switch (ext) {
            case "c", "h" -> new CLanguageParser(file);

            case "java" -> new JavaLanguageParser(
                    file,
                    sourceRoot
            );

            default -> throw new IllegalArgumentException(
                    "Unsupported file type: " + ext
            );
        };
    }

    private static String getExtension(File file) {
        String name = file.getName();
        int idx = name.lastIndexOf('.');
        return idx == -1 ? "" : name.substring(idx + 1).toLowerCase();
    }
}
