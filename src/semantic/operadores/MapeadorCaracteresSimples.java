package semantic.operadores;

public class MapeadorCaracteresSimples {

    public static String obtenerTipoOperador(String lexema) {
        if (lexema == null) {
            return "DESCONOCIDO";
        }

        switch (lexema) {
            case "+":
                return "OPERADOR_SUMA";
            case "-":
                return "OPERADOR_RESTA";
            case "*":
                return "OPERADOR_MULTIPLICACION";
            case "/":
                return "OPERADOR_DIVISION";
            case "=":
                return "ASIGNACION";
            case ";":
            case ",":
            case "(":
            case ")":
                return "SIMBOLO_ESPECIAL";
            default:
                return "DESCONOCIDO";
        }
    }
}
