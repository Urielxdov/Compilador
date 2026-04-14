package lexer.handlers;

import lexer.Context;
import lexer.Token;
import lexer.constants.TablaPalabrasReservadas;
import lexer.constants.TiposTokens;


/**
 * Handler lexico encargado de reconocer palabras reservadas
 * - Las palabras reservadas se definen en el Context
 * - Este handler solo extrae lexemas que coincidan con palabras reservadas
 */
public class PalabrasReservadasHandler implements TokenHandler {

    @Override
    public boolean accept(char c) {
        return Character.isLetter(c);
    }
    

    private boolean accept(String cadena) {
        return TablaPalabrasReservadas.existe(cadena);
    }

    /**
     * Extrae un lexema que corresponda a una palabra reservada
     * Devuelve null si no se reconoce ninguna palabra reservada
     */
    public Token extractLexeme(Context ctx) {
        String linea = ctx.getLineaActual();
        int inicio = ctx.getPunteroInicial();
        int pos = inicio;

        // Solo letras consecutivas
        while (pos < linea.length() && accept(linea.charAt(pos))) pos++;

        if (pos == inicio) return null; // No hay letras

        String lexema = linea.substring(inicio, pos);

        // Verifica si es palabra reservada
        if (accept(lexema)) {
            return new Token(TablaPalabrasReservadas.getValor(lexema), lexema, TiposTokens.PALABRA_RESERVADA);  
        }

        return null; // No es palabra reservada
    }
}