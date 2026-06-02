package intermediate;

import assembler.DataType;
import semantic.ast.*;
import semantic.operations.*;
import java.util.List;
import java.util.stream.Collectors;

public class PostfixPrinter {

    // Contador global para generar etiquetas únicas (L0, L1, L2...)
    private int labelCounter = 0;

    public void print(ProgramNode program) {
        System.out.println("\n=== NOTACION POSTFIJA (RPN) ===");
        // Reiniciamos el contador cada vez que imprimimos un programa completo
        this.labelCounter = 0;
        for (StatementNode stmt : program.getStatements()) {
            System.out.println(stmtToRPN(stmt));
        }
    }

    private String stmtToRPN(StatementNode stmt) {
        if (stmt == null) return "";

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
            // 1. Generamos etiquetas únicas para el flujo de control
            int labelSino = labelCounter++;
            int labelFin = labelCounter++;

            // 2. Evaluamos la condición (deja el resultado booleano en el tope de la pila)
            BoolExprNode cond = ifNode.getCondition();
            String condicionRPN = exprRPN(cond.getLeft()) + " " + exprRPN(cond.getRight()) + " " + cond.getOperator();

            // 3. Traducimos el flujo completo a RPN estricto:
            // CONDICION -> ETIQUETA_SINO -> JMP_F -> RAMA_ENTONCES -> ETIQUETA_FIN -> JMP -> LABEL_SINO -> RAMA_SINO -> LABEL_FIN
            return condicionRPN + " "
                    + "L" + labelSino + " JMP_F "
                    + stmtToRPN(ifNode.getThenBranch()) + " "
                    + "L" + labelFin + " JMP "
                    + "L" + labelSino + " LABEL "
                    + stmtToRPN(ifNode.getElseBranch()) + " "
                    + "L" + labelFin + " LABEL";

        } else {
            throw new IllegalArgumentException("PostfixPrinter: tipo de sentencia no soportado: "
                    + stmt.getClass().getSimpleName());
        }
    }

    private String exprRPN(ExprNode expr) {
        if (expr == null || expr.getTokens() == null) return "";
        List<OperationToken> postfix =
                semantic.operations.PostfixConverter.convert(expr.getTokens());
        return postfix.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));
    }
}