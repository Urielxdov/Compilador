package lexer.handlers.errors;

/**
 * Representa un error lexico detectado durante el analisis
 * de la entrada
 *
 * Contiene informacion contextual suficiente para reportar
 * el error al usuario o a etapas posteriores del compilador
 */
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

    /**
     * Tipos de errores lexicos reconocidos por el analizador
     */
    public enum ErrorType {
        /**Simbolo no perteneciente al alfabeto del lenguaje*/
        CARACTER_INVALIDO,
        /**Secuencia numerica mal formada sin punto decimal*/
        NUMERO_NATURAL_INVALIDO,
        /**Numero con formato flotante incorrecto*/
        NUMERO_FLOTANTE_INVALIDO,
        /**Identificador que viola las reglas lexicas*/
        IDENTIFICADOR_INVALIDO,
        /**Token que no puede clasificarse*/
        TOKEN_DESCONOCIDO
    }


    @Override
    public String toString() {
        return "Error [" + tipo + "] en (" + linea + "," + columna + "): "
                + mensaje + " → '" + lexema + "'";
    }
}
