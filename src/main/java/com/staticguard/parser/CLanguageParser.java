package com.staticguard.parser;

import com.staticguard.CLexer;
import com.staticguard.CParser;
import com.staticguard.enums.Language;
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
}
