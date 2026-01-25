package com.staticguard.cli;

import picocli.CommandLine.ITypeConverter;

import java.util.Map;
import java.util.Set;

public class AllowedCallsConverter implements ITypeConverter<Map.Entry<String, Set<String>>> {
    @Override
    public Map.Entry<String, Set<String>> convert(String value) {
        String[] parts = value.split("=", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "Expected format: caller=callee1,callee2");
        }

        String caller = parts[0];
        Set<String> callees = Set.of(parts[1].split(","));

        return Map.entry(caller, callees);
    }
}
