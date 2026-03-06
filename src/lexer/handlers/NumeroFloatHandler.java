package lexer.handlers;

import lexer.Context;
import lexer.Token;
import lexer.constants.TiposTokens;

/**
 * Handler que reconoce números flotantes
 * - Secuencias de dígitos con un solo punto decimal
 * - Rechaza números con más de un punto
 * - No acepta letras inmediatamente después del primer dígito
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
            return null; // No empieza con dígito
        }

        int numeroPuntos = 0;

        while (pos < linea.length() && accept(linea.charAt(pos))) {
            if (linea.charAt(pos) == '.') numeroPuntos++;
            pos++;
        }

        // Validaciones
        if (numeroPuntos > 1) return null;         // más de un punto
        if (pos < linea.length() && Character.isLetter(linea.charAt(pos))) return null; // dígito seguido de letra

        String lexema = linea.substring(inicio, pos);

        // No permitir números que terminan con punto
        if (lexema.endsWith(".")) return null;

        return new Token(ATRIBUTO, lexema, TiposTokens.NUMERO_FLOTANTE);
    }
}