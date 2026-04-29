package codegen;

import semantic.ast.ASTNode;
import semantic.ast.NodeKind;

import java.util.ArrayList;
import java.util.List;

public class GeneradorCodigo {
    private List<Cuadruplo> codigo;
    private int tempCount;
    private int labelCount;
    private INotacion notacion;

    public GeneradorCodigo() {
        this.notacion = new NotacionCuadruplo();
    }

    public GeneradorCodigo(INotacion notacion) {
        this.notacion = notacion;
    }

    public void setNotacion(INotacion notacion) {
        this.notacion = notacion;
    }

    public List<Cuadruplo> generar(ASTNode raiz) {
        codigo = new ArrayList<>();
        tempCount = 0;
        labelCount = 0;
        generarNodo(raiz);
        emitir(OpKind.HALT, null, null, null);
        return codigo;
    }

    public void imprimir(List<Cuadruplo> lista) {
        for (Cuadruplo c : lista)
            System.out.println(notacion.representar(c));
    }

    private String nuevotemp() {
        return "t" + tempCount++;
    }

    private String nuevaEtiqueta() {
        return "L" + labelCount++;
    }

    private void emitir(OpKind op, String result, String arg1, String arg2) {
        codigo.add(new Cuadruplo(codigo.size(), op, result, arg1, arg2));
    }

    private String generarNodo(ASTNode nodo) {
        if (nodo == null) return null;
        NodeKind kind = nodo.getKind();

        switch (kind) {
            case PROGRAMA:
                for (ASTNode hijo : nodo.getHijos())
                    generarNodo(hijo);
                return null;

            case DECL_ENTERA:
                for (ASTNode hijo : nodo.getHijos())
                    emitir(OpKind.VARI, hijo.getToken().getLexema(), null, null);
                return null;

            case DECL_REAL:
                for (ASTNode hijo : nodo.getHijos())
                    emitir(OpKind.VARR, hijo.getToken().getLexema(), null, null);
                return null;

            case ASIGNACION: {
                String id = nodo.getHijos().get(0).getToken().getLexema();
                String lugar = generarNodo(nodo.getHijos().get(1));
                emitir(OpKind.ASSIGN, id, lugar, null);
                return null;
            }

            case LEER: {
                String id = nodo.getHijos().get(0).getToken().getLexema();
                emitir(OpKind.READ, id, null, null);
                return null;
            }

            case ESCRIBIR: {
                String lugar = generarNodo(nodo.getHijos().get(0));
                emitir(OpKind.WRITE, lugar, null, null);
                return null;
            }

            case SI_SINO: {
                String condLugar = generarNodo(nodo.getHijos().get(0));
                String etiqElse = nuevaEtiqueta();
                String etiqFin  = nuevaEtiqueta();
                emitir(OpKind.IF_FALSE, condLugar, etiqElse, null);
                generarNodo(nodo.getHijos().get(1));
                emitir(OpKind.GOTO, null, etiqFin, null);
                emitir(OpKind.LABEL, etiqElse, null, null);
                generarNodo(nodo.getHijos().get(2));
                emitir(OpKind.LABEL, etiqFin, null, null);
                return null;
            }

            case EXPR_BIN: {
                String arg1 = generarNodo(nodo.getHijos().get(0));
                String arg2 = generarNodo(nodo.getHijos().get(1));
                String temp  = nuevotemp();
                emitir(mapOperador(nodo.getToken().getLexema()), temp, arg1, arg2);
                return temp;
            }

            case EXPR_BOOL: {
                String arg1 = generarNodo(nodo.getHijos().get(0));
                String arg2 = generarNodo(nodo.getHijos().get(1));
                String temp  = nuevotemp();
                emitir(mapOpRel(nodo.getToken().getLexema()), temp, arg1, arg2);
                return temp;
            }

            case NEG: {
                String arg  = generarNodo(nodo.getHijos().get(0));
                String temp = nuevotemp();
                emitir(OpKind.NEG, temp, arg, null);
                return temp;
            }

            case ID:
            case NUM_INT:
            case NUM_REAL:
                return nodo.getToken().getLexema();

            default:
                throw new RuntimeException("NodeKind no manejado: " + kind);
        }
    }

    private OpKind mapOperador(String op) {
        switch (op) {
            case "+": return OpKind.ADDR;
            case "-": return OpKind.REST;
            case "*": return OpKind.MULT;
            case "/": return OpKind.DIV;
            default: throw new RuntimeException("Operador no reconocido: '" + op + "'");
        }
    }

    private OpKind mapOpRel(String op) {
        switch (op) {
            case ">":  return OpKind.MAYOR;
            case "<":  return OpKind.MENOR;
            case "<>": return OpKind.DIST;
            case "==": return OpKind.IGUAL;
            default: throw new RuntimeException("Operador relacional no reconocido: '" + op + "'");
        }
    }
}
