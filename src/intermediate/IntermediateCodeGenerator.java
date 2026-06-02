package intermediate;

import assembler.DataType;
import semantic.ast.*;
import semantic.operations.*;
import java.util.*;

public class IntermediateCodeGenerator {
    private final List<Triplet> triplets = new ArrayList<>();
    private int tempCount = 0;
    private int labelCount = 0;
    private int currentSrcLine = 0;

    public IntermediateCode generate(ProgramNode program) {
        for (StatementNode stmt : program.getStatements()) {
            processStatement(stmt);
        }
        return new IntermediateCode(triplets);
    }

    private void processStatement(StatementNode stmt) {
        if (stmt == null) return;

        currentSrcLine = stmt.getLineNumber();

        if (stmt instanceof DeclarationNode decl) {
            String tipo = decl.getType() == DataType.ENTERO ? "Int" : "Real";
            for (String id : decl.getIds()) emit(tipo, id);

        } else if (stmt instanceof AssignmentNode assign) {
            String result = evalExpr(assign.getExpression());
            emit("=", assign.getTarget(), result);

        } else if (stmt instanceof ReadNode read) {
            for (String var : read.getVars()) emit("Leer", var);

        } else if (stmt instanceof WriteNode write) {
            if (write.getExpressions() != null) {
                for (ExprNode expr : write.getExpressions()) {
                    if (expr.isSingleIdentifier()) {
                        emit("Mostrar", expr.getSingleIdentifier());
                    } else {
                        String result = evalExpr(expr);
                        emit("Mostrar", result);
                    }
                }
            }
        } else if (stmt instanceof IfNode ifNode) {
            processIf(ifNode);
        } else {
            throw new IllegalArgumentException("ICG: tipo de sentencia no soportado: "
                    + stmt.getClass().getSimpleName());
        }
    }

    private String evalExpr(ExprNode expr) {
        if (expr == null) return "";

        List<OperationToken> tokens = expr.getTokens();
        if (tokens == null || tokens.isEmpty()) return "";

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
        return stack.isEmpty() ? "" : stack.pop();
    }

    private void processIf(IfNode ifNode) {
        int ifLine = currentSrcLine;
        BoolExprNode cond = ifNode.getCondition();
        if (cond == null) return;

        String leftVar  = evalExpr(cond.getLeft());
        String rightVar = evalExpr(cond.getRight());
        String labelElse = newLabel();
        String labelEnd  = newLabel();

        // 1. Condición de salto
        String condStr = leftVar + " " + cond.getOperator() + " " + rightVar;
        emit("JMP_F", condStr, labelElse);

        // 2. Ejecutar cuerpo del THEN directamente
        processStatement(ifNode.getThenBranch());

        // 3. Salto para brincarse el SINO
        currentSrcLine = ifLine;
        emit("JMP", labelEnd);

        // 4. Destino del primer salto si la condición fue falsa
        emit("LABEL", labelElse);

        // 5. Ejecutar cuerpo del ELSE directamente
        processStatement(ifNode.getElseBranch());

        // 6. Destino final
        currentSrcLine = ifLine;
        emit("LABEL", labelEnd);
    }

    private void emit(String i, String o1, String o2) {
        triplets.add(new Triplet(i, o1, o2, currentSrcLine));
    }

    private void emit(String i, String o1) {
        triplets.add(new Triplet(i, o1, null, currentSrcLine));
    }

    private String newTemp()  { return "t" + (tempCount++); }
    private String newLabel() { return "L" + (labelCount++); }
}