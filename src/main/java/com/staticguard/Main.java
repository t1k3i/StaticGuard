package com.staticguard;

import com.staticguard.cli.CLIOptions;
import picocli.CommandLine;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int exitCode = new CommandLine(new CLIOptions())
                .execute(args);
        System.exit(exitCode);
    }
}