package lexer;

public class Symbol {
    private String lexema;
    private String tipo;
    private String categoria;
    private int scopeLevel;
    private Object valor;

    public Symbol(String lexema, String tipo, String categoria, int scopeLevel, Object valor) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.categoria = categoria;
        this.scopeLevel = scopeLevel;
        this.valor = valor;
    }

    public String getLexema() {
        return lexema;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getScopeLevel() {
        return scopeLevel;
    }


    public Object getValor() {
        return valor;
    }
}
