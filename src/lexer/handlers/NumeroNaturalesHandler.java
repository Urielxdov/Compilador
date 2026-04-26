package lexer.handlers;

import lexer.Context;
import lexer.Token;
import lexer.constants.TiposTokens;

/**
 * Handler que reconoce numeros naturales
 * - Solo digitos
 * - Permite 0 como literal valido
 * - Rechaza enteros con ceros a la izquierda
 */
public class NumeroNaturalesHandler implements TokenHandler {
    private final int ATRIBUTO = 297;

    @Override
    public boolean accept(char c) {
        return Character.isDigit(c);
    }

    @Override
    public Token extractLexeme(Context ctx) {
        String linea = ctx.getLineaActual();
        int inicio = ctx.getPunteroInicial();
        int pos = inicio;

        if (pos >= linea.length() || !Character.isDigit(linea.charAt(pos))) {
            return null;
        }

        boolean empiezaCero = linea.charAt(pos) == '0';
        pos++;

        while (pos < linea.length() && accept(linea.charAt(pos))) {
            pos++;
        }

        if (empiezaCero && (pos - inicio) > 1) {
            return null;
        }

        if (pos < linea.length()) {
            char next = linea.charAt(pos);
            if (next == '.') {
                return null;
            }
            if (Character.isLetter(next)) {
                return null;
            }
        }

        String lexema = linea.substring(inicio, pos);
        return new Token(ATRIBUTO, lexema, TiposTokens.NUMERO_NATURAL);
    }
}
