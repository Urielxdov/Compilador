package lexer.table;

import lexer.Token;
import lexer.constants.TiposTokens;

import java.util.ArrayList;
import java.util.List;

public class SymbolInputTable {
    private String lexema;
    private TiposTokens tipo;
    private int idToken;          // el atributo del Token
    private int repeticiones;
    private List<Integer> lineas; // lista de líneas donde aparece
    private String valorInicial;  // primer lexema visto (útil si hay mayús/minús distintas)

    public SymbolInputTable(Token token, int numeroLinea) {
        this.lexema      = token.getLexema();
        this.tipo        = token.getTipo();
        this.idToken     = token.getAtributo();
        this.repeticiones = 1;
        this.lineas      = new ArrayList<>();
        this.lineas.add(numeroLinea);
        this.valorInicial = token.getLexema();
    }

    /** Registra una nueva aparición del mismo lexema */
    public void registrarAparicion(int numeroLinea) {
        repeticiones++;
        if (!lineas.contains(numeroLinea)) {
            lineas.add(numeroLinea);
        }
    }

    // Getters...
    public String getLexema()       { return lexema; }
    public TiposTokens getTipo()    { return tipo; }
    public int getIdToken()         { return idToken; }
    public int getRepeticiones()    { return repeticiones; }
    public List<Integer> getLineas(){ return lineas; }
    public String getValorInicial() { return valorInicial; }

    @Override
    public String toString() {
        return String.format("%-15s | %-12s | %3d | %3d | %s | %s",
                lexema, tipo, idToken, repeticiones,
                lineas.toString(), valorInicial);
    }
}
