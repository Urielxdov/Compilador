package lexer.handlers.errors;

public class LexicalError {
    private final int linea;
    private final int columna;
    private final String lexema;
    private final String mensaje;
    private final ErrorType tipo;

    public LexicalError(int linea, int columna, String lexema, String mensaje, ErrorType tipo) {
        this.linea = linea;
        this.columna = columna;
        this.lexema = lexema;
        this.mensaje = mensaje;
        this.tipo = tipo;
    }

    public enum ErrorType {
        CARACTER_INVALIDO,
        NUMERO_NATURAL_INVALIDO,
        NUMERO_FLOTANTE_INVALIDO,
        IDENTIFICADOR_INVALIDO,
        TOKEN_DESCONOCIDO
    }


    @Override
    public String toString() {
        return "Error [" + tipo + "] en (" + linea + "," + columna + "): "
                + mensaje + " → '" + lexema + "'";
    }
}
