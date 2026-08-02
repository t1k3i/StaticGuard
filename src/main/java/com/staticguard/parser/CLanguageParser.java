package com.staticguard.parser;

import com.staticguard.CLexer;
import com.staticguard.CParser;
import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.Language;
import com.staticguard.handlers.CHandler;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.IOException;

public class CLanguageParser extends LanguageParser<ParseTree> {

    public CLanguageParser(File file) {
        super(file, Language.C);
    }

    @Override
    public ParseTree parse() throws IOException {
        CharStream input = CharStreams.fromFileName(file.getAbsolutePath());
        CLexer lexer = new CLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CParser parser = new CParser(tokens);
        return parser.compilationUnit();
    }

    @Override
    public void handle(CLIOptionsConfig config, RuleContext context, ProjectContext projectContext) throws Exception {
        System.out.println("Parsing file: " + file.getName());
        var root = parse();
        System.out.println("Parsing succeeded.");
        new CHandler().handle(root, config, context, projectContext);
    }
}
