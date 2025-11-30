package com.staticguard.analyzers;

public interface Analyzer<T> {
    void runVisitor(T ast);

    default void postVisit(T ast) {
        // Default: do nothing
    }
}
