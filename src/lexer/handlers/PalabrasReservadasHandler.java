package lexer.handlers;

import lexer.Context;

public class PalabrasReservadasHandler implements TokenHandler {
    private final int ATRIBUTO = 405;
    @Override
    public boolean accept(char c) {
        return (c >= 97 && c <= 122) || (c >= 65 && c <= 90);
    }

    @Override
    public boolean proccessChar(Context ctx) {
        String linea = ctx.getLineaActual();
        int pos = ctx.getPunteroFinal();

        if (pos >= linea.length()) return false;



        return false;
    }
}
