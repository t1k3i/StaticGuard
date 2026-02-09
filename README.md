# StaticGuard

StaticGuard is a static analysis CLI tool designed primarily for **educational use**.  
It helps instructors and students enforce **assignment constraints**, check **good programming practices**, and collect **informational metrics** from source code.

The tool is currently focused on **Java** and **C**.

## Supported Languages

### Java
Full support for all analysis rules and assignment constraints.

### C
Supports core analysis metrics and constraints, with some language-specific limitations (e.g., no class dependency analysis).

---

## Usage

```bash
staticguard <source-file-or-directory> [options]
```

### 1. Global Options

| Option | Description | Required | Notes |
|--------|-------------|----------|-------|
| `--lang <java\|c>` | Specifies the project language. | **Yes** (only for projects) | Must be explicitly set to `java` or `c`. |
| `--dev` | Runs a single development/test visitor. | No | Useful for debugging new rules. |

### 2. Informational Visitors (`--info`)
Collects metrics and structural data.

- **`--info`**: Runs all informational visitors below.
- **`--call-graph`**: Generates a method call graph.
- **`--class-deps`**: Analyzes class coupling and dependencies (**Java only**).
- **`--used-types`**: Reports all types used in the project.
- **`--loop-nesting`**: Detects deeply nested loops to identify complexity.

### 3. Good Practices (`--good-practices`)
Enforces maintainability and coding standards.

- **`--good-practices`**: Runs all good practice visitors.
- **`--naming`**: Checks standard naming conventions (e.g., camelCase for methods).
- **`--unused-locals`**: Detects declared but unused local variables.
- **`--unused-imports`**: Detects unused imports (**Java only**).
- **`--long-methods[=<lines>]`**: Flags methods exceeding a line count threshold.
  - Default: `30` lines.
  - Example: `--long-methods=50`

### 4. Forbidden Rules (Constraints)
Strictly forbids specific language constructs. *Useful for assignment constraints.*

| Option | Argument Format | Description |
|--------|-----------------|-------------|
| `--forbid-methods` | `m1,m2` | Disallows calls to specific methods (e.g., `System.exit`). |
| `--forbid-types` | `T1,T2` | Disallows usage of specific types (e.g., `ArrayList`). |
| `--deny` | `Caller=Callee` | Disallows `Caller` from calling `Callee`. <br>Example: `--deny Controller=System.out.println` |
| `--forbid-control-flow`| `<RULES>`| Disallows specific control flow keywords (see below). |
| `--forbid-field-access`| N/A | Disallows direct field access to enforce encapsulation. **Java Only**. |
| `--primitive-mode` | `<MODE>` | Enforces primitive usage rules. **Java Only**. |

---

### Detailed Enum Values

#### Control Flow Rules (`--forbid-control-flow`)
Refers to the `ControlFlowRule` enum.
Use these values to restrict specific keywords:

| Value | Description | Language |
|-------|-------------|----------|
| `BREAK` | Helper for `break` statements. | All |
| `CONTINUE` | Helper for `continue` statements. | All |
| `RETURN` | Helper for `return` statements. | All |
| `INSTANCEOF`| Helper for `instanceof` checks. | **Java Only** |
| `GOTO` | Helper for `goto` statements. | **C Only** |

**Example:**
```bash
# Forbid break and goto in a C project
--forbid-control-flow BREAK,GOTO
```

#### Primitive Modes (`--primitive-mode`)
Refers to `PrimitiveTypeVisitor.Mode`. **Java Only**.

| Mode | Description |
|------|-------------|
| `ONLY_PRIMITIVE` | Forces the use of primitives (e.g., `int`). **Forbids** wrappers (`Integer`). |
| `NO_PRIMITIVE` | **Forbids** primitives (`int`). Forces the use of wrappers (`Integer`). |

---

## Language Compatibility Matrix

### Java Support
Full support for all flags.
- **unique features**:
  - `--class-deps`
  - `--unused-imports` (Not applicable to C inclusions in the same way)
  - `--forbid-field-access`
  - `--primitive-mode`
  - `INSTANCEOF` in control flow.

### C Support
The C handler supports most core features but skips object-oriented specific checks.

**Supported:**
- Analysis: `--call-graph`, `--used-types`, `--loop-nesting`
- Practices: `--naming`, `--long-methods`, `--unused-locals`
- Constraints: `--forbid-methods`, `--forbid-types`, `--deny`, `--forbid-control-flow` (includes `GOTO`)

**Skipped (Warning emitted):**
- `--class-deps`
- `--unused-imports`
- `--forbid-field-access`
- `--primitive-mode`

---

# Examples

## Run all checks on a Java project
staticguard src --lang java --all

## Run only informational analysis with detailed logs
staticguard MyClass.java --info

## Run good practices with a stricter long-method rule
staticguard src --lang java --good-practices --long-methods=20

## Enforce strict assignment constraints (Java)
staticguard src --lang java \
    --forbid-methods System.out.println \
    --forbid-control-flow RETURN,INSTANCEOF \
    --forbid-field-access \
    --primitive-mode ONLY_PRIMITIVE
