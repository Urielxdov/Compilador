# Código Intermedio, Postfija y Optimizaciones — Plan de Implementación

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add intermediate code generation (triplets), full-program postfix (RPN) printer, 4 local optimization passes, and a 3-phase global optimizer between semantic analysis and code generation.

**Architecture:** New packages `intermediate` and `optimizer` sit between `SemanticAnalyzer` and `AssemblerDriver`. All new classes work on `List<Triplet>` derived from the existing `ProgramNode` AST. `AssemblerDriver` is unchanged — it still uses the original AST; the optimizations are shown as console output.

**Tech Stack:** Java 17+ (switch expressions, pattern matching for instanceof, `List.of`, `stream().toList()`). No external libraries. No test framework — verification is by running `Main` and checking console output.

---

## File Map

| File | Action | Responsibility |
|------|--------|---------------|
| `src/intermediate/Triplet.java` | Create | Data: (instruccion, op1, op2) + tabular toString |
| `src/intermediate/IntermediateCode.java` | Create | Immutable List<Triplet> wrapper + imprimir() |
| `src/intermediate/IntermediateCodeGenerator.java` | Create | AST → IntermediateCode via postfix stack |
| `src/intermediate/PostfixPrinter.java` | Create | AST → full program in RPN, one line per statement |
| `src/optimizer/LocalOptimizer.java` | Create | 4 local passes: redundant, dead code, CSE, algebraic |
| `src/optimizer/BasicBlock.java` | Create | Sequence of Triplets with successor labels |
| `src/optimizer/FlowGraph.java` | Create | Build + print basic-block graph from triplet list |
| `src/optimizer/GlobalOptimizer.java` | Create | Source normalization + flow graph + constant propagation |
| `src/Main.java` | Modify | Wire new phases into runPipeline after semantic OK |

---

## Compilation command (run from project root)

```bash
javac -d out -sourcepath src $(find src -name "*.java") && java -cp out Main
```

---

## Task 1: Triplet + IntermediateCode

**Files:**
- Create: `src/intermediate/Triplet.java`
- Create: `src/intermediate/IntermediateCode.java`

- [ ] **Step 1.1 — Create `Triplet.java`**

```java
package intermediate;

import java.util.Objects;

public class Triplet {
    private final String instruccion;
    private final String op1;
    private final String op2;

    public Triplet(String instruccion, String op1, String op2) {
        this.instruccion = instruccion;
        this.op1 = op1;
        this.op2 = op2;
    }

    public Triplet(String instruccion, String op1) { this(instruccion, op1, null); }
    public Triplet(String instruccion) { this(instruccion, null, null); }

    public String getInstruccion() { return instruccion; }
    public String getOp1()        { return op1; }
    public String getOp2()        { return op2; }

    public Triplet withOp1(String newOp1) { return new Triplet(instruccion, newOp1, op2); }
    public Triplet withOp2(String newOp2) { return new Triplet(instruccion, op1, newOp2); }

    @Override
    public String toString() {
        String c1 = String.format("%-12s", instruccion);
        String c2 = op1 != null ? String.format("%-10s", op1) : "          ";
        String c3 = op2 != null ? op2 : "";
        return c1 + c2 + c3;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Triplet t)) return false;
        return instruccion.equals(t.instruccion)
            && Objects.equals(op1, t.op1)
            && Objects.equals(op2, t.op2);
    }

    @Override
    public int hashCode() { return Objects.hash(instruccion, op1, op2); }
}
```

- [ ] **Step 1.2 — Create `IntermediateCode.java`**

```java
package intermediate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IntermediateCode {
    private final List<Triplet> triplets;

    public IntermediateCode(List<Triplet> triplets) {
        this.triplets = Collections.unmodifiableList(new ArrayList<>(triplets));
    }

    public List<Triplet> getTriplets() { return triplets; }

    public void imprimir() {
        System.out.printf("%-12s %-10s %s%n", "Instruccion", "Op1", "Op2");
        System.out.println("-".repeat(36));
        for (Triplet t : triplets) System.out.println(t);
    }
}
```

- [ ] **Step 1.3 — Compile to verify no errors**

```bash
javac -d out -sourcepath src src/intermediate/Triplet.java src/intermediate/IntermediateCode.java
```

Expected: no errors.

- [ ] **Step 1.4 — Commit**

```bash
git add src/intermediate/Triplet.java src/intermediate/IntermediateCode.java
git commit -m "feat: add Triplet and IntermediateCode data classes"
```

---

## Task 2: IntermediateCodeGenerator

**Files:**
- Create: `src/intermediate/IntermediateCodeGenerator.java`

- [ ] **Step 2.1 — Create `IntermediateCodeGenerator.java`**

```java
package intermediate;

import assembler.DataType;
import semantic.ast.*;
import semantic.operations.*;
import java.util.*;

public class IntermediateCodeGenerator {
    private final List<Triplet> triplets = new ArrayList<>();
    private int tempCount = 0;
    private int labelCount = 0;

    public IntermediateCode generate(ProgramNode program) {
        for (StatementNode stmt : program.getStatements())
            processStatement(stmt);
        return new IntermediateCode(triplets);
    }

    private void processStatement(StatementNode stmt) {
        if (stmt instanceof DeclarationNode decl) {
            String tipo = decl.getType() == DataType.ENTERO ? "Int" : "Real";
            for (String id : decl.getIds()) emit(tipo, id);

        } else if (stmt instanceof AssignmentNode assign) {
            String result = evalExpr(assign.getExpression());
            emit("=", assign.getTarget(), result);

        } else if (stmt instanceof ReadNode read) {
            for (String var : read.getVars()) emit("Leer", var);

        } else if (stmt instanceof WriteNode write) {
            for (ExprNode expr : write.getExpressions()) {
                if (expr.isSingleIdentifier()) {
                    emit("Mostrar", expr.getSingleIdentifier());
                } else {
                    String result = evalExpr(expr);
                    emit("Mostrar", result);
                }
            }
        } else if (stmt instanceof IfNode ifNode) {
            processIf(ifNode);
        }
    }

    /**
     * Converts ExprNode to postfix, then processes via operand stack → emits triplets.
     * Single-token expressions (literal or identifier) emit no triplets; just return the value.
     */
    private String evalExpr(ExprNode expr) {
        List<OperationToken> tokens = expr.getTokens();
        if (tokens.size() == 1) {
            OperationToken tok = tokens.get(0);
            if (tok instanceof IdentifierToken id) return id.getName();
            if (tok instanceof NumberToken num) return num.getValue();
        }

        List<OperationToken> postfix = semantic.operations.PostfixConverter.convert(tokens);
        Deque<String> stack = new ArrayDeque<>();

        for (OperationToken tok : postfix) {
            if (tok instanceof IdentifierToken id) {
                stack.push(id.getName());
            } else if (tok instanceof NumberToken num) {
                stack.push(num.getValue());
            } else if (tok instanceof OperatorToken op) {
                String right = stack.pop();
                String left  = stack.pop();
                String temp  = newTemp();
                emit("mov", temp, left);
                emit(op.toString(), temp, right);
                stack.push(temp);
            }
        }
        return stack.pop();
    }

    /**
     * Pattern emitted for Si/Entonces/Sino:
     *   JMP_F  "left op right"  labelElse    ← jump to else when condition FALSE
     *   <then-branch>
     *   JMP    labelEnd
     *   LABEL  labelElse
     *   <else-branch>
     *   LABEL  labelEnd
     */
    private void processIf(IfNode ifNode) {
        BoolExprNode cond = ifNode.getCondition();
        String leftVar  = evalExpr(cond.getLeft());
        String rightVar = evalExpr(cond.getRight());
        String labelElse = newLabel();
        String labelEnd  = newLabel();

        String condStr = leftVar + " " + cond.getOperator() + " " + rightVar;
        emit("JMP_F", condStr, labelElse);

        processStatement(ifNode.getThenBranch());
        emit("JMP", labelEnd);

        emit("LABEL", labelElse);
        processStatement(ifNode.getElseBranch());
        emit("LABEL", labelEnd);
    }

    private void emit(String i, String o1, String o2) { triplets.add(new Triplet(i, o1, o2)); }
    private void emit(String i, String o1)            { triplets.add(new Triplet(i, o1, null)); }
    private String newTemp()  { return "t" + (tempCount++); }
    private String newLabel() { return "L" + (labelCount++); }
}
```

- [ ] **Step 2.2 — Wire temporarily into `Main.java` after semantic OK to verify output**

In `Main.java`, inside `runPipeline`, after the `[OK] Analisis semantico exitoso.` line add:

```java
// Phase 3: Intermediate Code (temporary verification)
IntermediateCodeGenerator icg = new IntermediateCodeGenerator();
IntermediateCode ic = icg.generate(result.getProgram());
System.out.println("\n=== CODIGO INTERMEDIO (CRUDO) ===");
ic.imprimir();
```

Also add the import at top of `Main.java`:
```java
import intermediate.IntermediateCode;
import intermediate.IntermediateCodeGenerator;
```

- [ ] **Step 2.3 — Compile and run**

```bash
javac -d out -sourcepath src $(find src -name "*.java") && java -cp out Main
```

Expected output for `programa1.txt` (sumador):
```
=== CODIGO INTERMEDIO (CRUDO) ===
Instruccion  Op1        Op2
------------------------------------
Int          a
Int          b
Int          suma
Leer         a
Leer         b
mov          t0         a
+            t0         b
=            suma       t0
Mostrar      suma
```

Expected output for `programa2.txt` (multiplicador):
```
Int          x
Int          y
Int          resultado
=            x          5
=            y          3
mov          t0         x
*            t0         y
=            resultado  t0
Mostrar      resultado
```

Expected output for `programa3.txt` (comparador):
```
Int          a
Int          b
Leer         a
Leer         b
JMP_F        a > b      L0
Mostrar      a
JMP          L1
LABEL        L0
Mostrar      b
LABEL        L1
```

- [ ] **Step 2.4 — Commit**

```bash
git add src/intermediate/IntermediateCodeGenerator.java src/Main.java
git commit -m "feat: add IntermediateCodeGenerator and wire CI output into pipeline"
```

---

## Task 3: PostfixPrinter + wire into Main

**Files:**
- Create: `src/intermediate/PostfixPrinter.java`
- Modify: `src/Main.java`

- [ ] **Step 3.1 — Create `PostfixPrinter.java`**

```java
package intermediate;

import assembler.DataType;
import semantic.ast.*;
import semantic.operations.*;
import java.util.List;
import java.util.stream.Collectors;

public class PostfixPrinter {

    public void print(ProgramNode program) {
        System.out.println("\n=== NOTACION POSTFIJA (RPN) ===");
        for (StatementNode stmt : program.getStatements())
            System.out.println(stmtToRPN(stmt));
    }

    private String stmtToRPN(StatementNode stmt) {
        if (stmt instanceof DeclarationNode decl) {
            String tipo = decl.getType() == DataType.ENTERO ? "Entero" : "Real";
            return decl.getIds().stream()
                .map(id -> id + " " + tipo)
                .collect(Collectors.joining(" "));

        } else if (stmt instanceof AssignmentNode assign) {
            return exprRPN(assign.getExpression()) + " " + assign.getTarget() + " =";

        } else if (stmt instanceof ReadNode read) {
            return read.getVars().stream()
                .map(v -> v + " Leer")
                .collect(Collectors.joining(" "));

        } else if (stmt instanceof WriteNode write) {
            return write.getExpressions().stream()
                .map(e -> exprRPN(e) + " Mostrar")
                .collect(Collectors.joining(" "));

        } else if (stmt instanceof IfNode ifNode) {
            BoolExprNode cond = ifNode.getCondition();
            return exprRPN(cond.getLeft()) + " "
                + exprRPN(cond.getRight()) + " "
                + cond.getOperator()
                + " Entonces " + stmtToRPN(ifNode.getThenBranch())
                + " Sino "     + stmtToRPN(ifNode.getElseBranch())
                + " FinSi";
        }
        return "";
    }

    private String exprRPN(ExprNode expr) {
        List<OperationToken> postfix =
            semantic.operations.PostfixConverter.convert(expr.getTokens());
        return postfix.stream()
            .map(Object::toString)
            .collect(Collectors.joining(" "));
    }
}
```

- [ ] **Step 3.2 — Add PostfixPrinter call in `Main.java`** after `ic.imprimir()`:

```java
import intermediate.PostfixPrinter;
// ...
PostfixPrinter pfp = new PostfixPrinter();
pfp.print(result.getProgram());
```

- [ ] **Step 3.3 — Compile and run**

```bash
javac -d out -sourcepath src $(find src -name "*.java") && java -cp out Main
```

Expected additional output for `programa1.txt`:
```
=== NOTACION POSTFIJA (RPN) ===
a Entero b Entero suma Entero
a Leer b Leer
a b + suma =
suma Mostrar
```

Expected for `programa3.txt`:
```
a Entero b Entero
a Leer
b Leer
a b > Entonces a Mostrar Sino b Mostrar FinSi
```

- [ ] **Step 3.4 — Commit**

```bash
git add src/intermediate/PostfixPrinter.java src/Main.java
git commit -m "feat: add PostfixPrinter (RPN) and wire into pipeline"
```

---

## Task 4: LocalOptimizer (4 passes)

**Files:**
- Create: `src/optimizer/LocalOptimizer.java`
- Modify: `src/Main.java`

- [ ] **Step 4.1 — Create `LocalOptimizer.java`**

```java
package optimizer;

import intermediate.IntermediateCode;
import intermediate.Triplet;
import java.util.*;

public class LocalOptimizer {

    public IntermediateCode optimize(IntermediateCode ic) {
        List<Triplet> current = new ArrayList<>(ic.getTriplets());

        current = runPass("Pase 1 - Subexpresiones Redundantes", current, this::pase1Redundantes);
        current = runPass("Pase 2 - Codigo Muerto", current, this::pase2CodigoMuerto);
        current = runPass("Pase 3 - Reutilizacion CSE", current, this::pase3CSE);
        current = runPass("Pase 4 - Reducciones Algebraicas", current, this::pase4Algebraicas);

        return new IntermediateCode(current);
    }

    private List<Triplet> runPass(String nombre, List<Triplet> antes,
                                  java.util.function.UnaryOperator<List<Triplet>> pass) {
        System.out.println("\n=== OPTIMIZACION LOCAL: " + nombre + " ===");
        System.out.println("ANTES:");
        antes.forEach(t -> System.out.println("  " + t));
        List<Triplet> despues = pass.apply(antes);
        System.out.println("DESPUES:");
        despues.forEach(t -> System.out.println("  " + t));
        return despues;
    }

    // -----------------------------------------------------------------------
    // Pase 1: Exact duplicate (mov Ti A, op Ti B) pairs → replace later Ti with first Ti
    // -----------------------------------------------------------------------
    private List<Triplet> pase1Redundantes(List<Triplet> in) {
        List<Triplet> result = new ArrayList<>(in);
        Map<String, String> exprToTemp = new LinkedHashMap<>(); // "A op B" -> first temp
        Map<String, String> replacement = new HashMap<>();       // redundant temp -> first temp
        Set<Integer> toRemove = new HashSet<>();

        for (int i = 0; i + 1 < result.size(); i++) {
            Triplet t1 = result.get(i);
            Triplet t2 = result.get(i + 1);
            if (!isMovToTemp(t1) || !isBinaryOpOnTemp(t2, t1.getOp1())) continue;

            String key = t1.getOp2() + " " + t2.getInstruccion() + " " + t2.getOp2();
            String temp = t1.getOp1();

            if (exprToTemp.containsKey(key)) {
                replacement.put(temp, exprToTemp.get(key));
                toRemove.add(i);
                toRemove.add(i + 1);
            } else {
                exprToTemp.put(key, temp);
            }
        }

        return applyReplacements(filterOut(result, toRemove), replacement);
    }

    // -----------------------------------------------------------------------
    // Pase 2: Dead temps — (mov Ti X, op Ti Y) where Ti never used afterwards
    // -----------------------------------------------------------------------
    private List<Triplet> pase2CodigoMuerto(List<Triplet> in) {
        Set<Integer> toRemove = new HashSet<>();

        for (int i = 0; i + 1 < in.size(); i++) {
            Triplet t1 = in.get(i);
            Triplet t2 = in.get(i + 1);
            if (!isMovToTemp(t1) || !isBinaryOpOnTemp(t2, t1.getOp1())) continue;

            String temp = t1.getOp1();
            boolean usedAfter = false;
            for (int j = i + 2; j < in.size(); j++) {
                Triplet t = in.get(j);
                if (temp.equals(t.getOp2())
                    || (temp.equals(t.getOp1()) && !isMovToTemp(t) && !"=".equals(t.getInstruccion()))) {
                    usedAfter = true;
                    break;
                }
            }
            if (!usedAfter) {
                toRemove.add(i);
                toRemove.add(i + 1);
            }
        }

        return filterOut(in, toRemove);
    }

    // -----------------------------------------------------------------------
    // Pase 3: CSE — if ops-chain of Ti is a suffix of Tj's ops-chain (only for + and *)
    // Example: T1 = X + 10  (chain: mov t1 X, + t1 10)
    //          T2 = Y + X + 10  (chain: mov t2 Y, + t2 X, + t2 10)
    //          Suffix [(+,X),(+,10)] of T2 matches T1's ops → replace with: + t2 t1
    // -----------------------------------------------------------------------
    private List<Triplet> pase3CSE(List<Triplet> in) {
        // Map each temp -> list of its defining triplet indices
        Map<String, List<Integer>> defIdx = new LinkedHashMap<>();
        for (int i = 0; i < in.size(); i++) {
            Triplet t = in.get(i);
            if (isTemp(t.getOp1())) defIdx.computeIfAbsent(t.getOp1(), k -> new ArrayList<>()).add(i);
        }

        // Build ops-chain for each temp (skip the "mov" initializer, keep binary ops)
        Map<String, List<String[]>> chains = new LinkedHashMap<>();
        for (var entry : defIdx.entrySet()) {
            List<String[]> ops = new ArrayList<>();
            for (int idx : entry.getValue()) {
                Triplet t = in.get(idx);
                if (!isMovToTemp(t)) ops.add(new String[]{t.getInstruccion(), t.getOp2()});
            }
            if (!ops.isEmpty()) chains.put(entry.getKey(), ops);
        }

        List<Triplet> result = new ArrayList<>(in);
        Set<Integer> toRemove = new HashSet<>();

        outer:
        for (var ei : chains.entrySet()) {
            String ti = ei.getKey();
            List<String[]> sigI = ei.getValue();

            for (var ej : chains.entrySet()) {
                String tj = ej.getKey();
                if (ti.equals(tj)) continue;
                List<String[]> sigJ = ej.getValue();

                if (sigJ.size() <= sigI.size()) continue;
                if (!isCommutativeChain(sigI)) continue; // only safe for + and *
                if (!endsWith(sigJ, sigI)) continue;

                // Find the actual triplet indices for the matching suffix in tj's chain
                List<Integer> idxJ = defIdx.get(tj);
                List<Integer> suffixIdxs = new ArrayList<>();
                int opCount = 0;
                for (int k = idxJ.size() - 1; k >= 0 && opCount < sigI.size(); k--) {
                    int idx = idxJ.get(k);
                    if (!isMovToTemp(in.get(idx))) {
                        suffixIdxs.add(0, idx);
                        opCount++;
                    }
                }
                if (suffixIdxs.size() != sigI.size()) continue;

                // Replace: remove all but last suffix index, overwrite last with "sigI[0].op tj ti"
                for (int k = 0; k < suffixIdxs.size() - 1; k++) toRemove.add(suffixIdxs.get(k));
                int lastIdx = suffixIdxs.get(suffixIdxs.size() - 1);
                result.set(lastIdx, new Triplet(sigI.get(0)[0], tj, ti));
                break outer; // one CSE substitution per pass call
            }
        }

        return filterOut(result, toRemove);
    }

    private boolean isCommutativeChain(List<String[]> ops) {
        return ops.stream().allMatch(o -> "+".equals(o[0]) || "*".equals(o[0]));
    }

    private boolean endsWith(List<String[]> longer, List<String[]> shorter) {
        int offset = longer.size() - shorter.size();
        for (int i = 0; i < shorter.size(); i++) {
            if (!longer.get(offset + i)[0].equals(shorter.get(i)[0])
                || !longer.get(offset + i)[1].equals(shorter.get(i)[1])) return false;
        }
        return true;
    }

    // -----------------------------------------------------------------------
    // Pase 4: Algebraic simplifications (identity operations)
    // -----------------------------------------------------------------------
    private List<Triplet> pase4Algebraicas(List<Triplet> in) {
        List<Triplet> result = new ArrayList<>();
        for (Triplet t : in) {
            String op  = t.getInstruccion();
            String op2 = t.getOp2();

            if (("+".equals(op) || "-".equals(op)) && "0".equals(op2)) continue; // X ± 0 = X
            if (("*".equals(op) || "/".equals(op)) && "1".equals(op2)) continue; // X */÷ 1 = X
            if ("*".equals(op) && "0".equals(op2)) {                              // X * 0 = 0
                result.add(new Triplet("mov", t.getOp1(), "0"));
                continue;
            }
            result.add(t);
        }
        return result;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------
    private boolean isTemp(String s) { return s != null && s.matches("t\\d+"); }
    private boolean isMovToTemp(Triplet t) { return "mov".equals(t.getInstruccion()) && isTemp(t.getOp1()); }
    private boolean isBinaryOpOnTemp(Triplet t, String temp) {
        return temp.equals(t.getOp1()) && t.getOp2() != null && !"mov".equals(t.getInstruccion());
    }

    private List<Triplet> filterOut(List<Triplet> list, Set<Integer> indices) {
        List<Triplet> out = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) if (!indices.contains(i)) out.add(list.get(i));
        return out;
    }

    private List<Triplet> applyReplacements(List<Triplet> list, Map<String, String> map) {
        if (map.isEmpty()) return list;
        return list.stream()
            .map(t -> {
                String o1 = map.getOrDefault(t.getOp1(), t.getOp1());
                String o2 = map.getOrDefault(t.getOp2(), t.getOp2());
                return new Triplet(t.getInstruccion(), o1, o2);
            })
            .toList();
    }
}
```

- [ ] **Step 4.2 — Wire LocalOptimizer into `Main.java`** after `ic.imprimir()` and `pfp.print(...)`:

```java
import optimizer.LocalOptimizer;
// ...
LocalOptimizer localOpt = new LocalOptimizer();
IntermediateCode icOptimizado = localOpt.optimize(ic);
```

- [ ] **Step 4.3 — Compile and run**

```bash
javac -d out -sourcepath src $(find src -name "*.java") && java -cp out Main
```

Expected for `programa2.txt` (multiplicador — no redundant subexpressions):
- Pase 1, 2, 3: ANTES = DESPUES (no changes)
- Pase 4: ANTES = DESPUES (no algebraic identities)

Expected for `programa1.txt` (sumador): same — no optimization opportunities, all passes return unchanged lists.

(Optimizations will fire with programs that have repeated subexpressions or identity ops.)

- [ ] **Step 4.4 — Commit**

```bash
git add src/optimizer/LocalOptimizer.java src/Main.java
git commit -m "feat: add LocalOptimizer with 4 local passes"
```

---

## Task 5: BasicBlock + FlowGraph + GlobalOptimizer

**Files:**
- Create: `src/optimizer/BasicBlock.java`
- Create: `src/optimizer/FlowGraph.java`
- Create: `src/optimizer/GlobalOptimizer.java`
- Modify: `src/Main.java`

- [ ] **Step 5.1 — Create `BasicBlock.java`**

```java
package optimizer;

import intermediate.Triplet;
import java.util.*;

public class BasicBlock {
    private final String label;
    private final List<Triplet> triplets;
    private final List<String> successors = new ArrayList<>();

    public BasicBlock(String label, List<Triplet> triplets) {
        this.label    = label;
        this.triplets = Collections.unmodifiableList(new ArrayList<>(triplets));
    }

    public String        getLabel()      { return label; }
    public List<Triplet> getTriplets()   { return triplets; }
    public List<String>  getSuccessors() { return successors; }
    public void addSuccessor(String s)   { if (!successors.contains(s)) successors.add(s); }
}
```

- [ ] **Step 5.2 — Create `FlowGraph.java`**

```java
package optimizer;

import intermediate.Triplet;
import java.util.*;

public class FlowGraph {
    private final List<BasicBlock> blocks;

    private FlowGraph(List<BasicBlock> blocks) { this.blocks = blocks; }

    public List<BasicBlock> getBlocks() { return blocks; }

    /**
     * Partition triplets into basic blocks following three leader rules:
     * 1. First instruction is a leader.
     * 2. Any instruction immediately after a JMP / JMP_F is a leader.
     * 3. Any LABEL triplet is a leader (it is the target of a jump).
     */
    public static FlowGraph build(List<Triplet> triplets) {
        if (triplets.isEmpty()) return new FlowGraph(List.of());

        Set<Integer> leaderSet = new TreeSet<>();
        leaderSet.add(0);
        for (int i = 0; i < triplets.size(); i++) {
            String instr = triplets.get(i).getInstruccion();
            if ("JMP".equals(instr) || "JMP_F".equals(instr)) {
                if (i + 1 < triplets.size()) leaderSet.add(i + 1);
            }
            if ("LABEL".equals(instr)) leaderSet.add(i);
        }

        List<Integer> leaders = new ArrayList<>(leaderSet);
        List<BasicBlock> blocks = new ArrayList<>();

        for (int b = 0; b < leaders.size(); b++) {
            int start = leaders.get(b);
            int end   = (b + 1 < leaders.size()) ? leaders.get(b + 1) : triplets.size();
            blocks.add(new BasicBlock("B" + b, new ArrayList<>(triplets.subList(start, end))));
        }

        // Build edges
        for (int b = 0; b < blocks.size(); b++) {
            BasicBlock block = blocks.get(b);
            List<Triplet> bt = block.getTriplets();
            if (bt.isEmpty()) continue;

            Triplet last = bt.get(bt.size() - 1);
            String instr = last.getInstruccion();

            if ("JMP".equals(instr)) {
                // Unconditional: edge to block that starts with LABEL <op1>
                findBlockWithLabel(blocks, last.getOp1())
                    .ifPresent(tgt -> block.addSuccessor(tgt.getLabel()));

            } else if ("JMP_F".equals(instr)) {
                // Conditional: fall-through AND jump target
                if (b + 1 < blocks.size()) block.addSuccessor(blocks.get(b + 1).getLabel());
                findBlockWithLabel(blocks, last.getOp2())
                    .ifPresent(tgt -> block.addSuccessor(tgt.getLabel()));

            } else if (!"LABEL".equals(instr)) {
                // Normal fall-through
                if (b + 1 < blocks.size()) block.addSuccessor(blocks.get(b + 1).getLabel());
            }
        }

        return new FlowGraph(blocks);
    }

    private static Optional<BasicBlock> findBlockWithLabel(List<BasicBlock> blocks, String labelName) {
        return blocks.stream()
            .filter(bl -> bl.getTriplets().stream()
                .anyMatch(t -> "LABEL".equals(t.getInstruccion())
                            && labelName.equals(t.getOp1())))
            .findFirst();
    }

    public void imprimir() {
        System.out.println("\n--- Fase 2: Bloques basicos + grafo de flujo ---");
        for (BasicBlock b : blocks) {
            System.out.println("BLOQUE " + b.getLabel() + ":");
            b.getTriplets().forEach(t -> System.out.println("  " + t));
        }
        System.out.print("Grafo: ");
        List<String> edges = new ArrayList<>();
        for (BasicBlock b : blocks)
            b.getSuccessors().forEach(s -> edges.add(b.getLabel() + " -> " + s));
        System.out.println(edges.isEmpty() ? "(sin saltos)" : String.join(", ", edges));
    }
}
```

- [ ] **Step 5.3 — Create `GlobalOptimizer.java`**

```java
package optimizer;

import intermediate.IntermediateCode;
import intermediate.Triplet;
import java.nio.file.*;
import java.util.*;

public class GlobalOptimizer {

    public IntermediateCode optimize(IntermediateCode ic, String sourcePath) {
        System.out.println("\n=== OPTIMIZACION GLOBAL ===");

        fase1Normalizacion(sourcePath);

        List<BasicBlock> blocks = fase2BloqueBasicos(ic);

        List<Triplet> optimized = fase3PropagacionConstantes(ic.getTriplets(), blocks);

        return new IntermediateCode(optimized);
    }

    // -----------------------------------------------------------------------
    // Fase 1: Remove blank lines, strip leading/trailing whitespace, show compact form
    // -----------------------------------------------------------------------
    private void fase1Normalizacion(String sourcePath) {
        System.out.println("\n--- Fase 1: Normalizacion fuente ---");
        try {
            List<String> lines = Files.readAllLines(Path.of(sourcePath));

            System.out.println("ANTES:");
            lines.forEach(l -> System.out.println("  |" + l + "|"));

            List<String> sinBlancos = lines.stream()
                .filter(l -> !l.isBlank())
                .map(String::trim)
                .toList();

            System.out.println("DESPUES (sin blancos/sangrias):");
            sinBlancos.forEach(l -> System.out.println("  |" + l + "|"));

            String compacto = String.join("", sinBlancos.stream()
                .map(l -> l.replace(" ", "")).toArray(String[]::new));
            System.out.println("DESPUES (compacto):");
            System.out.println("  " + compacto);

        } catch (Exception e) {
            System.out.println("  (no se pudo leer: " + e.getMessage() + ")");
        }
    }

    // -----------------------------------------------------------------------
    // Fase 2: Build and print basic blocks + flow graph
    // -----------------------------------------------------------------------
    private List<BasicBlock> fase2BloqueBasicos(IntermediateCode ic) {
        FlowGraph graph = FlowGraph.build(ic.getTriplets());
        graph.imprimir();
        return graph.getBlocks();
    }

    // -----------------------------------------------------------------------
    // Fase 3: Intra-block constant propagation
    //   If "= var C" (literal assignment) appears and var is not reassigned before use,
    //   replace "mov Ti var" with "mov Ti C".
    //   Reset known constants at label/jump boundaries (conservative).
    // -----------------------------------------------------------------------
    private List<Triplet> fase3PropagacionConstantes(List<Triplet> in,
                                                      List<BasicBlock> blocks) {
        System.out.println("\n--- Fase 3: Propagacion de Constantes ---");
        System.out.println("ANTES:");
        in.forEach(t -> System.out.println("  " + t));

        List<Triplet> result = new ArrayList<>(in);
        Map<String, String> constants = new HashMap<>(); // variable -> literal value

        for (int i = 0; i < result.size(); i++) {
            Triplet t = result.get(i);
            String instr = t.getInstruccion();

            if ("=".equals(instr) && t.getOp2() != null && isLiteral(t.getOp2())) {
                constants.put(t.getOp1(), t.getOp2());

            } else if ("LABEL".equals(instr) || "JMP".equals(instr) || "JMP_F".equals(instr)) {
                constants.clear(); // conservative: clear at control-flow boundaries

            } else if ("mov".equals(instr) && t.getOp2() != null
                       && constants.containsKey(t.getOp2())) {
                result.set(i, new Triplet("mov", t.getOp1(), constants.get(t.getOp2())));

            } else if ("=".equals(instr)) {
                // Variable reassigned to non-literal: invalidate it
                constants.remove(t.getOp1());
            }
        }

        System.out.println("DESPUES:");
        result.forEach(t -> System.out.println("  " + t));

        return result;
    }

    private boolean isLiteral(String val) {
        try { Integer.parseInt(val); return true; } catch (NumberFormatException ignored) {}
        try { Float.parseFloat(val); return true; } catch (NumberFormatException ignored) {}
        return false;
    }
}
```

- [ ] **Step 5.4 — Compile to verify no errors**

```bash
javac -d out -sourcepath src $(find src -name "*.java")
```

Expected: no errors.

- [ ] **Step 5.5 — Commit**

```bash
git add src/optimizer/BasicBlock.java src/optimizer/FlowGraph.java src/optimizer/GlobalOptimizer.java
git commit -m "feat: add BasicBlock, FlowGraph, and GlobalOptimizer"
```

---

## Task 6: Wire full pipeline in Main + final verification

**Files:**
- Modify: `src/Main.java`

- [ ] **Step 6.1 — Replace the temporary CI wiring with the full pipeline in `Main.java`**

Replace the current `runPipeline` method with:

```java
import intermediate.IntermediateCode;
import intermediate.IntermediateCodeGenerator;
import intermediate.PostfixPrinter;
import optimizer.GlobalOptimizer;
import optimizer.LocalOptimizer;
// ... existing imports unchanged ...

private static void runPipeline(Grammar grammar, LL1ParsingTable table, String path) {
    System.out.println("\n" + "=".repeat(60));
    System.out.println("Procesando: " + path);
    System.out.println("=".repeat(60));

    // Phase 1: Lexical + Syntactic + AST
    ASTBuilder astBuilder = new ASTBuilder();
    Lexer lexer = new Lexer(path);
    LL1Parser parser = new LL1Parser(grammar, table, lexer, astBuilder);
    parser.execute();

    if (!astBuilder.isParseOk()) {
        System.out.println("[ERROR SINTACTICO] Analisis detenido.");
        return;
    }
    System.out.println("[OK] Analisis sintactico exitoso.");

    // Phase 2: Semantic analysis
    SemanticAnalyzer analyzer = new SemanticAnalyzer();
    SemanticResult result = analyzer.analyze(astBuilder.getProgram());

    if (!result.isSuccess()) {
        System.out.println("[ERROR SEMANTICO]");
        for (String err : result.getErrors()) System.out.println("  " + err);
        return;
    }
    System.out.println("[OK] Analisis semantico exitoso.");

    // Phase 3: Intermediate code
    IntermediateCodeGenerator icg = new IntermediateCodeGenerator();
    IntermediateCode ic = icg.generate(result.getProgram());
    System.out.println("\n=== CODIGO INTERMEDIO (CRUDO) ===");
    ic.imprimir();

    // Phase 4: Postfix (RPN) representation
    new PostfixPrinter().print(result.getProgram());

    // Phase 5: Local optimizations (4 passes)
    IntermediateCode icLocal = new LocalOptimizer().optimize(ic);

    // Phase 6: Global optimization
    IntermediateCode icGlobal = new GlobalOptimizer().optimize(icLocal, path);
    System.out.println("\n[OK] Optimizaciones completadas.");

    // Phase 7: Code generation (uses original AST — unchanged)
    AssemblerDriver driver = new AssemblerDriver();
    driver.generate(result.getProgram());
    System.out.println("[OK] Codigo generado.");
    driver.getAssembler().imprimirResumen();
}
```

- [ ] **Step 6.2 — Compile and run all 3 programs**

```bash
javac -d out -sourcepath src $(find src -name "*.java") && java -cp out Main
```

Expected console sections per program (in order):
1. `[OK] Analisis sintactico exitoso.`
2. `[OK] Analisis semantico exitoso.`
3. `=== CODIGO INTERMEDIO (CRUDO) ===` — triplets table
4. `=== NOTACION POSTFIJA (RPN) ===` — one RPN line per statement
5. `=== OPTIMIZACION LOCAL: Pase 1 ...` through Pase 4 — each with ANTES/DESPUES
6. `=== OPTIMIZACION GLOBAL ===` — 3 sub-phases with ANTES/DESPUES
7. `[OK] Optimizaciones completadas.`
8. `[OK] Codigo generado.` + assembler summary

For `programa2.txt`: the assignment `x = 5` should appear as `= x 5` in the CI.
After global Fase 3 (constant propagation): `= x 5` means x is known as 5, so `mov t0 x` should become `mov t0 5`.

- [ ] **Step 6.3 — Verify programa3.txt shows correct basic blocks and flow graph**

Expected basic blocks for comparador:
```
BLOQUE B0:
  Int          a
  Int          b
  Leer         a
  Leer         b
  JMP_F        a > b      L0
BLOQUE B1:
  Mostrar      a
  JMP          L1
BLOQUE B2:
  LABEL        L0
  Mostrar      b
BLOQUE B3:
  LABEL        L1
Grafo: B0 -> B1, B0 -> B2, B1 -> B3
```

- [ ] **Step 6.4 — Commit**

```bash
git add src/Main.java
git commit -m "feat: wire full intermediate code + optimization pipeline in Main"
```

---

## Self-Review Checklist

- [x] Spec section 3 (Triplet + ICG) → Task 1 + Task 2
- [x] Spec section 4 (PostfixPrinter) → Task 3
- [x] Spec section 5 (4 local passes) → Task 4
- [x] Spec section 6.1 (source normalization) → Task 5 GlobalOptimizer fase1
- [x] Spec section 6.2 (basic blocks + flow graph) → Task 5 FlowGraph + GlobalOptimizer fase2
- [x] Spec section 6.3 (constant propagation) → Task 5 GlobalOptimizer fase3
- [x] Spec section 7 (console output format) → each task wires into Main with correct headers
- [x] AssemblerDriver unchanged → Task 6 only modifies Main, keeps AssemblerDriver call identical
- [x] Types consistent: `IntermediateCode(List<Triplet>)` used in all tasks; `LocalOptimizer.optimize(IntermediateCode)` matches `GlobalOptimizer.optimize(IntermediateCode, String)`
