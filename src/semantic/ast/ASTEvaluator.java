package semantic.ast;

public class ASTEvaluator {

    public int evaluar(ASTNode nodo) {
        if (nodo.getDerecho() == null && nodo.getIzquierdo() == null) {
            return Integer.parseInt(nodo.getToken().getLexema());
        }

        int izquierda = evaluar(nodo.getIzquierdo());
        int derecha = evaluar(nodo.getDerecho());

        String operador = nodo.getToken().getLexema();

        return switch (operador) {
            case "*" -> izquierda * derecha;
            case "+" -> izquierda + derecha;
            case "-" -> izquierda - derecha;
            case "/" -> izquierda / derecha;
            default -> throw new RuntimeException("Operador desconocido");
        };
    }
}
