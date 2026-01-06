package parser.ll1;

import lexer.Token;
import lexer.constants.TablaCaracteresSimples;
import lexer.constants.TablaPalabrasReservadas;
import lexer.constants.TiposTokens;
import parser.grammar.Symbol;

public class Comparator {

    public static boolean comapare (Token token, Symbol symbol) {
        String value = symbol.getNombre();
        switch (value) {
            case "id":
                return token.getTipo().equals(TiposTokens.IDENTIFICADOR);
            case "intLiteral":
                return token.getTipo().equals(TiposTokens.NUMERO_NATURAL) || token.getTipo().equals(TiposTokens.NUMERO_FLOTANTE);
            default:
                return TablaPalabrasReservadas.existe(value) || TablaCaracteresSimples.existe(value);
        }
    }
}
