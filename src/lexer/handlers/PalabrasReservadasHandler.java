package lexer.handlers;

import lexer.Context;

/**
 * Nota de diseño:
 * El reconocimiento de plabras reservadas se realiza dentro de
 * {@link IdentificadoresHandler} como parte de las reglas del lenguaje,
 * donde las palabras reservadas se distinguen por iniciar con mayuscula
 *
 * Esta clase se dejo como placeholder para posible separacion
 * futura de responsabilidades
 */
public class PalabrasReservadasHandler implements TokenHandler {
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
