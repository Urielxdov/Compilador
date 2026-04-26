package semantic;

import lexer.Token;
import lexer.constants.TiposTokens;
import lexer.tokens.NodoLineaToken;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SemanticReport {
    private final List<NodoLineaToken> tokensLinea;
    private final Map<String, TipoDato> tiposDeclarados;
    private final List<String> errores;
    private final List<VerificationTrace> verificaciones;

    public SemanticReport(List<NodoLineaToken> tokensLinea, Map<String, TipoDato> tiposDeclarados,
                          List<String> errores, List<VerificationTrace> verificaciones) {
        this.tokensLinea = tokensLinea;
        this.tiposDeclarados = tiposDeclarados;
        this.errores = errores;
        this.verificaciones = verificaciones;
    }

    public String renderSymbolTable() {
        Map<String, SemanticSymbolRow> filas = new LinkedHashMap<>();
        int siguienteId = 500;

        for (NodoLineaToken nodo : tokensLinea) {
            Token token = nodo.getToken();
            if (!esSimboloReportable(token)) {
                continue;
            }

            String lexema = token.getLexema();
            SemanticSymbolRow fila = filas.get(lexema);
            if (fila == null) {
                fila = crearFila(token, nodo.getLinea(), siguienteId);
                filas.put(lexema, fila);
                if (token.getTipo() == TiposTokens.IDENTIFICADOR) {
                    siguienteId++;
                }
            } else {
                fila.registrarLinea(nodo.getLinea());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s %-8s %-8s %-10s %-13s %s%n",
                "Lexema", "Tipo", "Id_lex", "Valor", "Repeticiones", "Num_linea"));
        sb.append("-".repeat(70)).append("\n");
        for (SemanticSymbolRow fila : filas.values()) {
            sb.append(fila).append("\n");
        }
        return sb.toString();
    }

    public String renderTypeVerification() {
        StringBuilder sb = new StringBuilder();

        if (verificaciones.isEmpty()) {
            sb.append("No se encontraron asignaciones para verificar.\n");
        } else {
            for (VerificationTrace verificacion : verificaciones) {
                sb.append(verificacion).append("\n");
            }
        }

        if (!errores.isEmpty()) {
            sb.append("Errores detectados:\n");
            for (String error : errores) {
                sb.append("- ").append(error).append("\n");
            }
        } else {
            sb.append("Sin errores de tipos.\n");
        }

        return sb.toString();
    }

    private boolean esSimboloReportable(Token token) {
        return token.getTipo() == TiposTokens.IDENTIFICADOR
                || token.getTipo() == TiposTokens.NUMERO_NATURAL
                || token.getTipo() == TiposTokens.NUMERO_REAL;
    }

    private SemanticSymbolRow crearFila(Token token, int linea, int idActual) {
        String lexema = token.getLexema();
        TipoDato tipo = token.getTipo() == TiposTokens.IDENTIFICADOR
                ? tiposDeclarados.getOrDefault(lexema, TipoDato.DESCONOCIDO)
                : TipoDato.desdeToken(token);

        Integer idLex = token.getTipo() == TiposTokens.IDENTIFICADOR ? idActual : null;
        String valor = token.getTipo() == TiposTokens.IDENTIFICADOR ? "-" : lexema;

        SemanticSymbolRow fila = new SemanticSymbolRow(lexema, tipo, idLex, valor);
        fila.registrarLinea(linea);
        return fila;
    }

    private static class SemanticSymbolRow {
        private final String lexema;
        private final TipoDato tipo;
        private final Integer idLex;
        private final String valor;
        private final List<Integer> lineas = new ArrayList<>();

        private SemanticSymbolRow(String lexema, TipoDato tipo, Integer idLex, String valor) {
            this.lexema = lexema;
            this.tipo = tipo;
            this.idLex = idLex;
            this.valor = valor;
        }

        private void registrarLinea(int linea) {
            lineas.add(linea);
        }

        @Override
        public String toString() {
            return String.format("%-12s %-8s %-8s %-10s %-13d %s",
                    lexema,
                    tipo,
                    idLex == null ? "-" : idLex.toString(),
                    valor,
                    lineas.size(),
                    unirLineas());
        }

        private String unirLineas() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lineas.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(lineas.get(i));
            }
            return sb.toString();
        }
    }
}
