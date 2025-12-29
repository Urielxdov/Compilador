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
}
