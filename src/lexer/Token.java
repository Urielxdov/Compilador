package lexer;

public class Token {
    private int atributo;
    private String lexema;

    public Token(int atributo, String lexema) {
        this.atributo = atributo;
        this.lexema = lexema;
    }

    public String getLexema() {
        return lexema;
    }

    public int getAtributo() {
        return atributo;
    }
}
