package lexer.handlers;

import lexer.Context;
import lexer.Token;
import lexer.constants.TiposTokens;

/**
 * Manejador lexico envargado de reconocer caracteres simples
 * y operadores compuestos de uno o dos simbolos (por ejemplo
 * '=', '==', '<>')
 *
 * Forma parte de la cadena de manejadores del analizador lexico
 */
public class CaracterSimpleHandler implements TokenHandler {
    /**Atributo lexico para el operador '=='*/
    private final int DOBLE_IGUAL = 600;
    /**Atributo lexico para el operador '<>'*/
    private final int DOBLE_FLECHA = 601;
    @Override
    public boolean accept(char c) {
        return (c == 59) || (c == 61) || (c == 43) || (c == 45)
                || (c == 42) || (c == 40) || (c == 41) || (c == 44)
                || (c == 60) || (c == 62);
    }

    @Override
    public Token extractLexeme(Context ctx) {
        
        String linea = ctx.getLineaActual();
        int pos = ctx.getPunteroInicial();
        if (pos >= linea.length()) return null;

        char c = linea.charAt(pos);
        if (!accept(c)) return null;

        String lexema = "" + c;
        if (pos + 1 < linea.length()) {
            char next = linea.charAt(pos + 1);
            String sublexema = lexema + next;
            if(sublexema.equals("==") || sublexema.equals("<>")) {
                lexema = sublexema;
            }
        }

        if (lexema.length() == 1) {
            return new Token(c, lexema, TiposTokens.CARACTER_SIMPLE);
        } else {
            return new Token(lexema.equals("<>") ? DOBLE_FLECHA : DOBLE_IGUAL, lexema, TiposTokens.CARACTER_SIMPLE);
        }
        
    }
}
