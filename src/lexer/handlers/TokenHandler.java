package lexer.handlers;

import lexer.Context;

/**
 * Interfaz base para los manejadores lexicos del analizador
 *
 * Cada implementacio representa una posible transicion del automata lexico
 * y forma parte de una cadena de responabilidad
 */
public interface TokenHandler {

    /**
     * Indica si el handler puede aplicar a partir del caracter actual
     *
     * Este metodo no consume caracteres ni modifica el contexto,
     * unicamente sirve como filtro rapido
     *
     * @param c carcater actual
     * @return true si el handler puede intentar procesar el lexema,
     *          false en caso contrario
     */
    boolean accept(char c);

    /**
     * Intenta reconocer un tojen a partir de la posicion actual del contexto
     *
     * Contrato:
     * - Retorna true si el handler consumio caraceres y decidio un token,
     * ya sea valido o invalido.
     * - Retorna false si el handler no aplica u no consumio carcteres,
     * permitiendo que el siguiente hanlder de la cadeana sea evaluado.
     *
     * El hanldler es responsable de actualizar el contexto lexico
     *
     * @param ctx contexto lexico compartido
     * @return true si el hanlder aplico, false si no aplica
     */
    boolean proccessChar(Context ctx);
}
