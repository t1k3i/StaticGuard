package com.staticguard.cli;

import com.staticguard.handlers.CHandler;
import com.staticguard.handlers.JavaHandler;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "staticguard",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "Static analysis for Java and C source files."
)
public class CLIOptions implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", description = "The source file to analyze")
    private File file;

    @CommandLine.Option(names = "--all", description = "Run all visitors")
    private boolean runAll;

    @CommandLine.Option(names = "--dev", description = "Run just one test visitor you are developing")
    private boolean development;

    @Override
    public Integer call() {
        CLIOptionsConfig config = new CLIOptionsConfig(runAll, true, development);

        try {
            LanguageParser<?> parser = ParserFactory.createParser(file);

            System.out.println("Parsing file: " + file.getName());
            Object ast = parser.parse();
            System.out.println("Parsing succeeded.");

            switch (parser.getLanguage()) {
                case C -> new CHandler().handle(ast, config);
                case JAVA -> new JavaHandler().handle(ast, config);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }

}
