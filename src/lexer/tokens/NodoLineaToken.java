package lexer.tokens;

import lexer.Token;

public class NodoLineaToken {
    private int linea;
    private Token token;

    public NodoLineaToken(int linea, Token token) {
        this.linea = linea;
        this.token = token;
    }

    public int getLinea() {
        return linea;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "(" + linea + ", " + token + ")";
    }
}