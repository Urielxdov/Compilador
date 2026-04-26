package lexer.table;

import lexer.Context;
import lexer.constants.TiposTokens;
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
        List<NodoLineaToken> tokens = context.getTokensLinea();

        for (int i = 0; i < tokens.size(); i++) {
            NodoLineaToken nlt = tokens.get(i);
            TiposTokens tipo = nlt.getToken().getTipo();
            if (!esTokenPermitido(tipo)) {
                continue;
            }

            String lexema = nlt.getToken().getLexema();
            SymbolInputTable entrada = tabla.entradas.get(lexema);

            if (entrada == null) {
                entrada = new SymbolInputTable(nlt.getToken(), nlt.getLinea());
                tabla.entradas.put(lexema, entrada);
            } else {
                entrada.registrarAparicion(nlt.getLinea());
            }

            if (tipo == TiposTokens.IDENTIFICADOR) {
                String valorAsignado = extraerValorInicialAsignado(tokens, i);
                if (valorAsignado != null) {
                    entrada.setValorInicial(valorAsignado);
                }
            }
        }

        return tabla;
    }

    private static boolean esTokenPermitido(TiposTokens tipo) {
        return tipo == TiposTokens.IDENTIFICADOR
                || tipo == TiposTokens.NUMERO_NATURAL
                || tipo == TiposTokens.NUMERO_REAL;
    }

    private static String extraerValorInicialAsignado(List<NodoLineaToken> tokens, int indiceActual) {
        int siguiente = indiceActual + 1;
        int siguienteNumero = indiceActual + 2;

        if (siguienteNumero >= tokens.size()) {
            return null;
        }

        NodoLineaToken tokenAsignacion = tokens.get(siguiente);
        NodoLineaToken tokenValor = tokens.get(siguienteNumero);

        if (tokenAsignacion.getToken().getTipo() == TiposTokens.CARACTER_SIMPLE
                && "=".equals(tokenAsignacion.getToken().getLexema())
                && (tokenValor.getToken().getTipo() == TiposTokens.NUMERO_NATURAL
                || tokenValor.getToken().getTipo() == TiposTokens.NUMERO_REAL)) {
            return tokenValor.getToken().getLexema();
        }

        return null;
    }

    public Collection<SymbolInputTable> getEntradas() {
        return entradas.values();
    }

    @Override
    public String toString() {
        if (entradas.isEmpty()) return "(Tabla vacía)\n";

        // ── Calcular anchos dinámicos por columna ────────────────────────────
        int wLex  = "Lexema".length();
        int wTipo = "Tipo".length();
        int wId   = "ID".length();
        int wRep  = "Rep".length();
        int wLin  = "Líneas".length();
        int wVal  = "Val. inicial".length();

        for (SymbolInputTable e : entradas.values()) {
            wLex  = Math.max(wLex,  e.getLexema().length());
            wTipo = Math.max(wTipo, e.getTipo().toString().length());
            wId   = Math.max(wId,   String.valueOf(e.getIdToken()).length());
            wRep  = Math.max(wRep,  String.valueOf(e.getRepeticiones()).length());
            wLin  = Math.max(wLin,  e.getLineas().size());
            wVal  = Math.max(wVal,  e.getValorInicial().length());
        }

        // ── Formato de fila ──────────────────────────────────────────────────
        String fmt = "  %-" + wLex  + "s  |  %-" + wTipo + "s  |  %"
                + wId   + "s  |  %"  + wRep  + "s  |  %-"
                + wLin  + "s  |  %-" + wVal  + "s%n";

        // ── Separador ────────────────────────────────────────────────────────
        int total = wLex + wTipo + wId + wRep + wLin + wVal + 7 * 4 + 5; // padding + pipes
        String sep    = "  " + "─".repeat(total) + "%n";
        String sepMid = "  " + "┼".repeat(0)     + "%n"; // reemplazado abajo

        // Separador con cruces alineadas a las columnas
        String linea = "  "
                + "─".repeat(wLex  + 4) + "┼"
                + "─".repeat(wTipo + 4) + "┼"
                + "─".repeat(wId   + 4) + "┼"
                + "─".repeat(wRep  + 4) + "┼"
                + "─".repeat(wLin  + 4) + "┼"
                + "─".repeat(wVal  + 4);

        // ── Construcción ─────────────────────────────────────────────────────
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%n  TABLA DE SÍMBOLOS  (%d entradas)%n", entradas.size()));
        sb.append("  ").append("═".repeat(total)).append("\n");
        sb.append(String.format(fmt, "Lexema", "Tipo", "ID", "Rep", "Líneas", "Val. inicial"));
        sb.append(linea).append("\n");

        for (SymbolInputTable e : entradas.values()) {
            sb.append(String.format(fmt,
                    e.getLexema(),
                    e.getTipo(),
                    e.getIdToken(),
                    e.getRepeticiones(),
                    e.getLineas(),
                    e.getValorInicial()));
        }

        sb.append("  ").append("═".repeat(total)).append("\n");
        return sb.toString();
    }
}
