package lexer;

import lexer.constants.TiposTokens;

/**
 * Modelo para la creacion de tokens
 */
public class Token {
    // Valor numerico para los tokens
    private int atributo;
    // Valor del token
    private String lexema;
    // Categoria del analizador lexico
    private TiposTokens tipo;

    public Token(int atributo, String lexema, TiposTokens tipo) {
        this.atributo = atributo;
        this.lexema = lexema;
        this.tipo = tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public int getAtributo() {
        return atributo;
    }

    public TiposTokens getTipo () {
        return tipo;
    }

    @Override
    public String toString() {
        return "Lexema: " + lexema + " - " + atributo + " tipo: " + tipo;
    }

    /**
     * Sobrescritura del metodo equals donde se busca comparar un token con otro mediante el lexema
     * para la conexion con el analizador sintactico
     * @return - Equivalencia
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Token)) return false;
        if (((Token) o).lexema.equals(this.lexema)) return true;
        return false;
    }
}
