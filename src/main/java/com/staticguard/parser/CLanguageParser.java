package com.staticguard.parser;

import com.staticguard.CLexer;
import com.staticguard.CParser;
import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.Language;
import com.staticguard.handlers.CHandler;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.IOException;

public class CLanguageParser extends LanguageParser<ParseTree> {

    public CLanguageParser(File file) {
        super(file);
    }

    @Override
    public ParseTree parse() throws IOException {
        CharStream input = CharStreams.fromFileName(file.getAbsolutePath());
        CLexer lexer = new CLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CParser parser = new CParser(tokens);

        parser.removeErrorListeners();
        lexer.removeErrorListeners();

        BaseErrorListener errorListener = new BaseErrorListener() {
            @Override
            public void syntaxError(
                    Recognizer<?, ?> recognizer,
                    Object offendingSymbol,
                    int line,
                    int charPositionInLine,
                    String msg,
                    RecognitionException e
            ) {
                throw new IllegalArgumentException(
                        "Failed to parse " + file.getName()
                );
            }
        };

        lexer.addErrorListener(errorListener);
        parser.addErrorListener(errorListener);

        return parser.compilationUnit();
    }

    @Override
    public void handle(CLIOptionsConfig config, RuleContext context, ProjectContext projectContext) throws Exception {
        System.out.println();
        System.out.println("========================================");
        System.out.println("Parsing C file: " + file.getName());
        System.out.println("========================================");
        var root = parse();
        new CHandler().handle(root, config, context, projectContext);
    }
}
