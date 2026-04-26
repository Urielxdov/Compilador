package lexer.handlers;

import lexer.Context;
import lexer.Token;
import lexer.constants.TiposTokens;

/**
 * Handler que reconoce numeros flotantes
 * - Secuencias de digitos con un solo punto decimal
 * - Rechaza numeros con mas de un punto
 * - No acepta letras inmediatamente despues del primer digito
 */
public class NumeroFloatHandler implements TokenHandler {
    private final int ATRIBUTO = 296;

    @Override
    public boolean accept(char c) {
        return Character.isDigit(c) || c == '.';
    }

    @Override
    public Token extractLexeme(Context ctx) {
        String linea = ctx.getLineaActual();
        int inicio = ctx.getPunteroInicial();
        int pos = inicio;

        if (pos >= linea.length() || !Character.isDigit(linea.charAt(pos))) {
            return null;
        }

        int numeroPuntos = 0;

        while (pos < linea.length() && accept(linea.charAt(pos))) {
            if (linea.charAt(pos) == '.') {
                numeroPuntos++;
            }
            pos++;
        }

        if (numeroPuntos != 1) {
            return null;
        }
        if (pos < linea.length() && Character.isLetter(linea.charAt(pos))) {
            return null;
        }

        String lexema = linea.substring(inicio, pos);
        if (lexema.endsWith(".")) {
            return null;
        }

        return new Token(ATRIBUTO, lexema, TiposTokens.NUMERO_REAL);
    }
}
