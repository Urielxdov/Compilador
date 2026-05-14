package semantic;

import assembler.DataType;
import semantic.ast.*;
import semantic.operations.*;

import java.util.*;

public class SemanticAnalyzer {

    public SemanticResult analyze(ProgramNode program) {
        List<String> errors = new ArrayList<>();
        Map<String, DataType> symbolTable = new LinkedHashMap<>();

        for (StatementNode stmt : program.getStatements()) {
            checkStatement(stmt, symbolTable, errors);
        }

        return new SemanticResult(errors.isEmpty(), errors, program);
    }

    private void checkStatement(StatementNode stmt, Map<String, DataType> table, List<String> errors) {
        if (stmt instanceof DeclarationNode decl) {
            for (String id : decl.getIds()) {
                if (table.containsKey(id)) {
                    errors.add("Error semántico: variable '" + id + "' ya declarada.");
                } else {
                    table.put(id, decl.getType());
                }
            }
        } else if (stmt instanceof AssignmentNode assign) {
            String target = assign.getTarget();
            if (!table.containsKey(target)) {
                errors.add("Error semántico: variable '" + target + "' no declarada.");
            } else {
                DataType targetType = table.get(target);
                DataType exprType = inferType(assign.getExpression(), table, errors);
                if (!isCompatible(targetType, exprType)) {
                    errors.add("Error semántico: tipo incompatible en asignación a '" + target
                        + "' (esperado " + targetType + ", expresión es " + exprType + ").");
                }
            }
        } else if (stmt instanceof ReadNode read) {
            for (String var : read.getVars()) {
                if (!table.containsKey(var))
                    errors.add("Error semántico: variable '" + var + "' no declarada en Leer.");
            }
        } else if (stmt instanceof WriteNode write) {
            for (ExprNode expr : write.getExpressions()) {
                inferType(expr, table, errors);
            }
        } else if (stmt instanceof IfNode ifNode) {
            checkBoolExpr(ifNode.getCondition(), table, errors);
            checkStatement(ifNode.getThenBranch(), table, errors);
            checkStatement(ifNode.getElseBranch(), table, errors);
        }
    }

    private void checkBoolExpr(BoolExprNode bool, Map<String, DataType> table, List<String> errors) {
        DataType leftType  = inferType(bool.getLeft(),  table, errors);
        DataType rightType = inferType(bool.getRight(), table, errors);
        if (!isNumeric(leftType) || !isNumeric(rightType)) {
            errors.add("Error semántico: operandos de expresión booleana deben ser numéricos.");
        }
    }

    private DataType inferType(ExprNode expr, Map<String, DataType> table, List<String> errors) {
        DataType result = null;
        for (OperationToken tok : expr.getTokens()) {
            if (tok instanceof IdentifierToken id) {
                if (!table.containsKey(id.getName())) {
                    errors.add("Error semántico: variable '" + id.getName() + "' no declarada.");
                } else {
                    result = widen(result, table.get(id.getName()));
                }
            } else if (tok instanceof NumberToken num) {
                DataType litType = num.getValue().contains(".") ? DataType.REAL : DataType.ENTERO;
                result = widen(result, litType);
            }
        }
        return result == null ? DataType.ENTERO : result;
    }

    private DataType widen(DataType a, DataType b) {
        if (a == null) return b;
        if (b == null) return a;
        return (a == DataType.REAL || b == DataType.REAL) ? DataType.REAL : DataType.ENTERO;
    }

    private boolean isCompatible(DataType target, DataType exprType) {
        if (target == exprType) return true;
        if (target == DataType.REAL && exprType == DataType.ENTERO) return true;
        return false;
    }

    private boolean isNumeric(DataType t) {
        return t == DataType.ENTERO || t == DataType.REAL;
    }
}
