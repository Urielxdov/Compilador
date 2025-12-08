package lexer.handlers;

import lexer.Context;
import lexer.Token;
import lexer.handlers.errors.LexicalError;

public class CaracterSimpleHandler implements TokenHandler {
    @Override
    public boolean accept(char c) {
        return (c == 59) || (c == 61) || (c == 43) || (c == 45)
                || (c == 42) || (c == 40) || (c == 41) || (c == 44)
                || (c == 60) || (c == 62);
    }

    @Override
    public boolean proccessChar(Context ctx) {
        String linea = ctx.getLineaActual();
        int pos = ctx.getPunteroFinal();

        if (pos >= linea.length()) return false; // Sepa que ha pasao

        char c = linea.charAt(pos);

        if (!accept(c)) return false;

        while (pos < linea.length() && accept(linea.charAt(pos))) pos++;

        if (pos - ctx.getPunteroInicial() > 2) {
            ctx.setPunteroFinal(pos);
            ctx.agregarError(new LexicalError(ctx.getNumeroLinea(), pos, linea.substring(ctx.getPunteroInicial(), ctx.getPunteroFinal()), "Caracter no perteneciente a los caracteres simples", LexicalError.ErrorType.TOKEN_DESCONOCIDO));
            return false;
        }

        if (pos - ctx.getPunteroInicial() == 2) {
            String lexema = linea.substring(ctx.getPunteroInicial(), pos);
            ctx.setPunteroFinal(pos);
            if (lexema.equals("==") || lexema.equals("<>")) {
                ctx.agregarToken(new Token(c, lexema));
                return true;
            } else {
                ctx.agregarError(new LexicalError(ctx.getNumeroLinea(), pos, lexema, "Caracter no perteneciente a los caracteres simples", LexicalError.ErrorType.TOKEN_DESCONOCIDO));
                return false;
            }
        }
        if (pos > ctx.getPunteroInicial()) {
            ctx.setPunteroFinal(pos);
            ctx.agregarToken(new Token(c, linea.substring(ctx.getPunteroInicial(), ctx.getPunteroFinal())));
            return true;
        }


        return false;
    }
}
