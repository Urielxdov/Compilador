package lexer.handlers;

import lexer.Context;
import lexer.Token;
import lexer.constants.TiposTokens;

/**
 * Handler que reconoce números naturales
 * - Solo dígitos
 * - No puede empezar con '0' a menos que sea parte de un flotante
 * - No puede contener letras
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
            return null; // No empieza con dígito
        }

        boolean empiezaCero = linea.charAt(pos) == '0';
        pos++;

        while (pos < linea.length() && accept(linea.charAt(pos))) {
            pos++;
        }

        // Validaciones
        if (empiezaCero && (pos >= linea.length() || linea.charAt(pos) != '.')) {
            return null; // Número natural inválido que empieza con 0
        }

        if (pos < linea.length()) {
            char next = linea.charAt(pos);
            if (next == '.') return null;       // Flotante, lo maneja otro handler
            if (Character.isLetter(next)) return null; // Letra después de número -> inválido
        }

        String lexema = linea.substring(inicio, pos);

        return new Token(ATRIBUTO, lexema, TiposTokens.NUMERO_NATURAL);
    }
}