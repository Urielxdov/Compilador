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
        } else {
            throw new IllegalArgumentException("PostfixPrinter: tipo de sentencia no soportado: "
                + stmt.getClass().getSimpleName());
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
