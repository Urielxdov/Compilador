package lexer.handlers;

import javax.smartcardio.ATR;

import lexer.Context;
import lexer.Token;
import lexer.constants.TiposTokens;

/**
 * Manejador lexico para identificadores y palabras reservadas
 *
 * Nota: Por restricciones de tiempo, este handler combina
 * la logica de identificacion de palabras reservadas e identificadores,
 * lo cual podria separse en implementaciones futuras
 */
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
    public Token extractLexeme(Context ctx) {
        String linea = ctx.getLineaActual();
        int inicio = ctx.getPunteroInicial();
        int pos = inicio;

        if (pos >= linea.length() || !Character.isLetter(linea.charAt(pos)) || !accept(linea.charAt(pos))) {
            return null; // No puede empezar con dígito o _
        }

        while (pos < linea.length() && accept(linea.charAt(pos))) {
            pos++;
        }

        String lexema = linea.substring(inicio, pos);

        // Validación básica: no termina con _
        if (lexema.endsWith("_")) {
            return null;
        }

        return new Token(ATRIBUTO, lexema, TiposTokens.IDENTIFICADOR);
    }

    
}
