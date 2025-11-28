package com.staticguard;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzer.RuleContext;
import com.staticguard.parser.language.JavaLanguageParser;
import com.staticguard.visitors.java.JavaNamingVisitor;
import com.staticguard.visitors.java.LoopNestingVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("To little arguments");
            return;
        }

        File file = new File(args[0]);
        var parser = new JavaLanguageParser(file);

        CompilationUnit cu;
        try {
            cu = parser.parse();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        var ruleContext = new RuleContext();
        cu.accept(new JavaNamingVisitor(true), ruleContext);

        var issues = ruleContext.getIssues();

        for (var issue: issues) {
            System.out.println(issue);
        }

        var visitor = new LoopNestingVisitor();
        cu.accept(visitor, null);
        int maxNestedLoops = visitor.getMaxDepth();
        System.out.println("Maximum nested loops: " + maxNestedLoops);

        String modifiedSource = cu.toString();
        Path path = Paths.get(args[0]);
        Files.writeString(path, modifiedSource);

        System.out.println("Here");
    }
}