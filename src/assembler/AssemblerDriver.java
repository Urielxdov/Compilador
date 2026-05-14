package assembler;

import semantic.ast.*;
import semantic.operations.*;
import java.util.List;

/**
 * Recorre un ProgramNode (AST) y llama a la API de Assembler
 * para generar el código máquina correspondiente.
 */
public class AssemblerDriver {

    private final Assembler asm;
    private int tempCount = 0;

    public AssemblerDriver() {
        this.asm = new Assembler();
    }

    /**
     * Punto de entrada principal: genera código máquina para todo el programa.
     */
    public void generate(ProgramNode program) {
        asm.iniciarPrograma(program.getName());

        for (StatementNode stmt : program.getStatements()) {
            processStatement(stmt);
        }

        asm.terminarPrograma(program.getName());
    }

    // ---------------------------------------------------------------
    // Despacho de sentencias
    // ---------------------------------------------------------------

    private void processStatement(StatementNode stmt) {
        if (stmt instanceof DeclarationNode decl) {
            for (String id : decl.getIds()) {
                if (decl.getType() == DataType.ENTERO) {
                    asm.declararEntero(id);
                } else {
                    asm.declararReal(id);
                }
            }
        } else if (stmt instanceof AssignmentNode assign) {
            evaluateExpr(assign.getExpression());
            asm.guardar(assign.getTarget());
        } else if (stmt instanceof ReadNode read) {
            for (String var : read.getVars()) {
                asm.leer(var);
            }
        } else if (stmt instanceof WriteNode write) {
            for (ExprNode expr : write.getExpressions()) {
                if (expr.isSingleIdentifier()) {
                    asm.escribir(expr.getSingleIdentifier());
                } else {
                    String tmp = "__tmp" + (tempCount++);
                    asm.declararReal(tmp);
                    evaluateExpr(expr);
                    asm.guardar(tmp);
                    asm.escribir(tmp);
                }
            }
        } else if (stmt instanceof IfNode ifNode) {
            processIf(ifNode);
        } else {
            throw new AssemblerException("processStatement: tipo de sentencia no soportado: " + stmt.getClass().getSimpleName());
        }
    }

    // ---------------------------------------------------------------
    // Evaluación de expresiones (postfix)
    // ---------------------------------------------------------------

    /**
     * Convierte la expresión a postfix y emite instrucciones para cada token.
     * Para expresiones de un solo identificador se emite LOAD directamente.
     */
    private void evaluateExpr(ExprNode expr) {
        List<OperationToken> postfix =
                semantic.operations.PostfixConverter.convert(expr.getTokens());

        for (OperationToken tok : postfix) {
            if (tok instanceof IdentifierToken id) {
                asm.cargar(id.getName());
            } else if (tok instanceof NumberToken num) {
                String v = num.getValue();
                int intVal = v.contains(".")
                        ? (int) Float.parseFloat(v)
                        : Integer.parseInt(v);
                asm.moverInmediato(intVal);
            } else if (tok instanceof OperatorToken op) {
                asm.operacion(op.toString());
            }
        }
    }

    // ---------------------------------------------------------------
    // Condicionales
    // ---------------------------------------------------------------

    /**
     * Genera código para Si-Entonces-Sino con semántica correcta:
     *
     * Patrón emitido:
     *   LOAD left
     *   CMP  right
     *   JGT  → then_start     ; condición TRUE → salta a rama Entonces
     *   &lt;else-branch&gt;          ; rama Sino (fall-through cuando condición FALSE)
     *   JMP  → end             ; salta sobre rama Entonces
     *   then_start:
     *   &lt;then-branch&gt;          ; rama Entonces
     *   end:
     *
     * De este modo:
     *   - condición TRUE  → JGT dispara → ejecuta Entonces
     *   - condición FALSE → fall-through → ejecuta Sino, luego JMP a end
     */
    private void processIf(IfNode ifNode) {
        BoolExprNode cond = ifNode.getCondition();

        String leftVar  = getOrEvalVar(cond.getLeft());
        String rightVar = getOrEvalVar(cond.getRight());

        // Emite: LOAD leftVar, CMP rightVar, JGT → <then> (condición TRUE salta a Entonces)
        int pcSaltoCondicion = asm.compararYSaltar(leftVar, rightVar, cond.getOperator());

        // Rama Sino (fall-through cuando condición es FALSE)
        processStatement(ifNode.getElseBranch());

        // JMP incondicional para saltar la rama Entonces después de ejecutar Sino
        int pcSaltoFin = asm.saltoIncondicional();

        // Parchea el salto condicional para apuntar aquí (inicio de Entonces)
        asm.cerrarSalto(pcSaltoCondicion);

        // Rama Entonces (destino del JGT cuando condición es TRUE)
        processStatement(ifNode.getThenBranch());

        // Parchea el JMP incondicional para apuntar aquí (después de Entonces)
        asm.cerrarSalto(pcSaltoFin);
    }

    // ---------------------------------------------------------------
    // Utilidades
    // ---------------------------------------------------------------

    /**
     * Si la expresión es un identificador simple lo devuelve directamente.
     * Si no, la evalúa, almacena en una variable temporal y devuelve su nombre.
     */
    private String getOrEvalVar(ExprNode expr) {
        if (expr.isSingleIdentifier()) {
            return expr.getSingleIdentifier();
        }
        String tmp = "__ctmp" + (tempCount++);
        asm.declararReal(tmp);
        evaluateExpr(expr);
        asm.guardar(tmp);
        return tmp;
    }

    public Assembler getAssembler() { return asm; }
}
