package lexer;

import lexer.constants.TiposTokens;

public class Token {
    private int atributo;
    private String lexema;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Token)) return false;
        if (((Token) o).lexema.equals(this.lexema)) return true;
        return false;
    }
}
