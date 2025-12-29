package lexer.handlers;

import lexer.Context;
import lexer.Token;
import lexer.constants.TiposTokens;
import lexer.handlers.errors.LexicalError;

public class CaracterSimpleHandler implements TokenHandler {
    private final int DOBLE_IGUAL = 600;
    private final int DOBLE_FLECHA = 601;
    @Override
    public boolean accept(char c) {
        return (c == 59) || (c == 61) || (c == 43) || (c == 45)
                || (c == 42) || (c == 40) || (c == 41) || (c == 44)
                || (c == 60) || (c == 62);
    }

    @Override
    public boolean proccessChar(Context ctx) {
        String linea = ctx.getLineaActual();
        int pos = ctx.getPunteroInicial();

        if (pos >= linea.length()) return false;

        char c = linea.charAt(pos);

        if (!accept(c)) return false;

        int inicio = pos;
        pos++;

        String lexema = "" + c;

        if (pos < linea.length()) {
            char next = linea.charAt(pos);

            String sublexema = lexema + next;
            if(sublexema.equals("==") || sublexema.equals("<>")) {
                pos++;
                lexema = sublexema;
            }
        }

        ctx.setPunteroFinal(pos);

        if (!ctx.isSimpleCharacter(lexema)) {
//            ctx.agregarError(new LexicalError(
//                    ctx.getNumeroLinea(),
//                    pos,
//                    lexema,
//                    "Caracter irreconocible",
//                    LexicalError.ErrorType.TOKEN_DESCONOCIDO
//            ));
            ctx.setTokenActual(new Token(404, lexema, TiposTokens.INVALIDO));
            return false;
        }

        if (lexema.length() == 1) {
            // ctx.agregarToken();
            ctx.setTokenActual(new Token(lexema.charAt(0), lexema, TiposTokens.CARACTER_SIMPLE));
        } else {
            if (lexema.length() == 2) {
                int atributo = lexema.equals("==") ? DOBLE_IGUAL : DOBLE_FLECHA;
                //ctx.agregarToken();
                ctx.setTokenActual(new Token(atributo, lexema, TiposTokens.CARACTER_SIMPLE));
            }
        }
        return true;
    }
}
