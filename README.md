# StaticGuard

StaticGuard is a static analysis CLI tool designed primarily for **educational use**.  
It helps instructors and students enforce **assignment constraints**, check **good programming practices**, and collect **informational metrics** from source code.

The tool is currently focused on **Java** and **C**.

## Supported Languages

### Java

### C
TODO  

---

## Usage

```bash
staticguard <source-file-or-directory> [options]
```
- <source-file-or-directory>
A single source file or a directory containing a project.

# Global Options
--lang <java|c>
    Specifies the project language.
    Required when analyzing directories.
    Example:
        --lang java

--all
    Runs all visitors (informational, good practices, and forbidden rules).
    Example:
        --all

--dev
    Runs only a single development/test visitor.
    Intended for tool development and debugging.
    Example:
        --dev


# Informational Visitors (--info)
# Informational visitors collect structural or metric data and do not enforce rules.

--info
    Runs all informational visitors.
    Example:
        --info

--call-graph
    Analyzes the method call graph.
    Example:
        --call-graph

--class-deps
    Analyzes class dependencies and coupling.
    Example:
        --class-deps

--used-types
    Reports which types are used in the project.
    Example:
        --used-types

--loop-nesting
    Reports deeply nested loops (informational unless combined with thresholds).
    Example:
        --loop-nesting


# Good Practices (--good-practices)
# Good-practice visitors detect maintainability and style issues.

--good-practices
    Runs all good-practice visitors.
    Example:
        --good-practices

--unused-imports
    Detects unused imports.
    Example:
        --unused-imports

--unused-locals
    Detects unused local variables.
    Example:
        --unused-locals

--naming
    Checks Java naming conventions (classes, methods, variables).
    Example:
        --naming

--long-methods[=<maxLines>]
    Detects methods longer than a specified number of lines.
    Default: 30 lines
    Can be enabled without a value or with a custom threshold
    Examples:
        --long-methods
        --long-methods=50

--duplicate-code
    Detects duplicate method implementations using normalized method bodies.
    Example:
        --duplicate-code


# Forbidden Rules (Constraints)
# Forbidden rules enforce hard constraints, typically required in programming assignments.

--forbid-methods <method1,method2,...>
    Disallows calls to specific methods.
    Example:
        --forbid-methods System.out.println,System.exit

--forbid-types <type1,type2,...>
    Disallows usage of specific types.
    Example:
        --forbid-types int,Scanner

--deny <caller=callee1,callee2>
    Disallows specific method-call relationships.
    Example:
        --deny Service=System.out.println,Logger.log

--forbid-control-flow <rules>
    Disallows specific control-flow constructs.
    Available rules: BREAK, CONTINUE, RETURN, INSTANCEOF
    Example:
        --forbid-control-flow BREAK,CONTINUE

--forbid-field-access
    Disallows direct field access and enforces encapsulation.
    Example:
        --forbid-field-access

--primitive-mode <mode>
    Controls primitive type usage rules.
    Available modes depend on implementation (e.g., FORBID, WARN, etc.)
    Example:
        --primitive-mode FORBID


# Examples

# Run all checks on a Java project
staticguard src --lang java --all

# Run only informational analysis
staticguard MyClass.java --info

# Run good practices with a stricter long-method rule
staticguard src --lang java --good-practices --long-methods=20

# Enforce assignment constraints
staticguard src --lang java \
    --forbid-methods System.out.println \
    --forbid-control-flow RETURN,INSTANCEOF \
    --forbid-field-access
