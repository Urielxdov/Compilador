package semantic;

import lexer.Token;
import lexer.constants.TiposTokens;

public enum TipoDato {
    INT("int"),
    REAL("real"),
    ERROR("error"),
    DESCONOCIDO("desconocido");

    private final String nombre;

    TipoDato(String nombre) {
        this.nombre = nombre;
    }

    public static TipoDato desdeDeclaracion(String lexema) {
        if ("Int".equals(lexema) || "Entero".equals(lexema)) {
            return INT;
        }
        if ("Real".equals(lexema)) {
            return REAL;
        }
        return DESCONOCIDO;
    }

    public static TipoDato desdeToken(Token token) {
        if (token == null) {
            return DESCONOCIDO;
        }
        if (token.getTipo() == TiposTokens.NUMERO_NATURAL) {
            return INT;
        }
        if (token.getTipo() == TiposTokens.NUMERO_REAL) {
            return REAL;
        }
        return DESCONOCIDO;
    }

    public boolean esNumerico() {
        return this == INT || this == REAL;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
