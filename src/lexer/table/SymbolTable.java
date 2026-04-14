package lexer.table;

import lexer.Context;
import lexer.tokens.NodoLineaToken;
import java.util.*;

public class SymbolTable {

    private Map<String, SymbolInputTable> entradas = new LinkedHashMap<>();

    /**
     * Construye la tabla recorriendo los tokens ya reconocidos por el Context.
     * No modifica nada en Context.
     */
    public static SymbolTable construir(Context context) {
        SymbolTable tabla = new SymbolTable();
        for (NodoLineaToken nlt : context.getTokensLinea()) {
            String lexema = nlt.getToken().getLexema();
            if (tabla.entradas.containsKey(lexema)) {
                tabla.entradas.get(lexema).registrarAparicion(nlt.getLinea());
            } else {
                tabla.entradas.put(lexema,
                        new SymbolInputTable(nlt.getToken(), nlt.getLinea()));
            }
        }
        return tabla;
    }

    public Collection<SymbolInputTable> getEntradas() {
        return entradas.values();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-15s | %-12s | %3s | %3s | %-20s | %s%n",
                "Lexema", "Tipo", "ID", "Rep", "Líneas", "Val. inicial"));
        sb.append("-".repeat(75)).append("\n");
        for (SymbolInputTable e : entradas.values()) {
            sb.append(e).append("\n");
        }
        return sb.toString();
    }
}
