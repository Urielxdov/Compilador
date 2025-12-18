package lexer.handlers;

import lexer.Context;
import lexer.Token;
import lexer.constants.TiposTokens;
import lexer.handlers.errors.LexicalError;

public class IdentificadoresHandler implements TokenHandler {
    private final int ATRIBUTO = 295;
    @Override
    public boolean accept(char c) {
        return acceptLetters(c) || (c >= 48 && c <= 57) || (c == 95);
    }

    private boolean acceptLetters(char c) {
        return (c >= 97 && c <= 122);
    }

    @Override
    public boolean proccessChar(Context ctx) {
        String linea = ctx.getLineaActual();
        int pos = ctx.getPunteroFinal();

        if (pos >= linea.length()) return false;

        char c = linea.charAt(pos);

        if (acceptMayus(c)) return proccessReservedWord(ctx, linea, pos);
        if (!acceptLetters(c)) return false; // Posible error lexico dado que solo se puede comenzar con letras

        while (pos < linea.length() && accept(linea.charAt(pos))) pos++;

        ctx.setPunteroFinal(pos);
        if(ctx.limitador()) {
            String lexema = linea.substring(ctx.getPunteroInicial(), pos);
            if (lexema.charAt(lexema.length()-1) == '_') {
                ctx.agregarError(new LexicalError(
                        ctx.getNumeroLinea(),
                        pos,
                        lexema,
                        "Los identificadores no pueden terminar con guion bajo",
                        LexicalError.ErrorType.IDENTIFICADOR_INVALIDO
                ));
                return false;
            } else {
                ctx.agregarSimbolo(new Token(ATRIBUTO , lexema, TiposTokens.IDENTIFICADOR));
                return true;
            }
        } else {
            String lexema = ctx.consumirLexema();
            ctx.agregarError(new LexicalError(
                    ctx.getNumeroLinea(),
                    pos,
                    lexema,
                    "Se introdujo un caracteres desconocido en medio de un identificador",
                    LexicalError.ErrorType.IDENTIFICADOR_INVALIDO
            ));
            return false;
        }

    }

    private boolean acceptMayus(char c) {
        return (c >= 65 && c <= 90);
    }

    private boolean isLetter(char c) {
        return (c >= 97 && c <= 122);
    }

    private boolean proccessReservedWord (Context ctx, String linea, int pos) {
        pos++;
        while (pos < linea.length() && isLetter(linea.charAt(pos))) pos++;

        ctx.setPunteroFinal(pos);
        String lexema = linea.substring(ctx.getPunteroInicial(), pos);
        if (ctx.limitador()) {
            if (ctx.isReservedWord(lexema)) {
                ctx.agregarToken(new Token(800, lexema, TiposTokens.PALABRA_RESERVADA));
                return true;
            } else {
                ctx.agregarError(new LexicalError(ctx.getNumeroLinea(), pos, lexema,"No cumple con las condiciones de palabra reservada ni identificador", LexicalError.ErrorType.IDENTIFICADOR_INVALIDO));
                return false;
            }
        } else {
            ctx.agregarError(new LexicalError(ctx.getNumeroLinea(), pos, lexema,"No cumple con las condiciones de palabra reservada ni identificador", LexicalError.ErrorType.IDENTIFICADOR_INVALIDO));
            return false;
        }
    }
}
