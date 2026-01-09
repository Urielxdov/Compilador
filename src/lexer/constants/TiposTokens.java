package lexer.constants;

/**
 * TiposTokens
 *
 * Enum que define las categorias lexicas de tokens
 * reconocidas por el analizador lexico
 *
 * Cada valor representa un tipo semantico de token
 * utilizando como contrato entre el lexer y el parser
 */
public enum TiposTokens {
    NUMERO_NATURAL,
    NUMERO_FLOTANTE,
    IDENTIFICADOR,
    PALABRA_RESERVADA,
    CARACTER_SIMPLE,
    /**Secuencia de caracteres que no pertenece al lenguaje*/
    INVALIDO
}
