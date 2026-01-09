package lexer.handlers;

import lexer.Context;
import lexer.Token;
import lexer.constants.TiposTokens;
import lexer.handlers.errors.LexicalError;

/**
 * Handler lexico encargado de reconocer numeros flotantes
 *
 * Reglas:
 * -Acepta secuencias de digitos con unsolo punto decimal
 * -Rechaza numeros con mas de un punto
 * - Rechaza letras inmediatamente despues del primer numero
 *
 * Importante:
 * - Este hanlder asume que el reconocimiento de numeros naturales
 * ya fue evaluado previamente en la cadena de handlers.
 * - El orden de evaluacion de los handlers es critico para su correcto funcionamiento
 *
 * Nota tecnica:
 * -Debido a restricciones de tiempo, el manejo de errores lexicos se limita
 * a la generacion de tokens invalidos
 */
public class NumeroFloatHandler implements TokenHandler {
    private final int ATRIBUTO = 401;

    @Override
    public boolean accept(char c) {
        return ((c >= 48) && (c <= 57)) || (c == 46);
    }

    /**
     * Intenta reconocer un numero flotante a partir de la posicion actual del contexto
     *
     * @param ctx contexto lexico compartido
     * @return true si el handler consumio caracteres (validos o invalidos),
     *              false si el handler no aplica en la posicion actual
     */
    @Override
    public boolean proccessChar(Context ctx) {
        /**
         * Nota
         * A este punto ya se debio pasar por numeros naturales
         * por ende es imposible que si se detecta un primer numero
         * el segundo caracter es imposible que sea un float
         * No se dejara salir el lexema
         */
        int numeroPuntos = 0;
        String linea = ctx.getLineaActual();
        int pos = ctx.getPunteroFinal();

        if (ctx.getPunteroFinal() >= linea.length()) return false; // Ya vemos en ejecucion

        char c = linea.charAt(pos);

        if (!accept(c)) return false; // Posible error lexico esta vez

        while (pos < linea.length() && accept(linea.charAt(pos))){
            if(linea.charAt(pos) == '.') numeroPuntos++;
            pos++;
        }

        if (numeroPuntos >= 2) {
//            ctx.agregarError(new LexicalError(
//                    ctx.getNumeroLinea(),
//                    pos,
//                    linea.substring(ctx.getPunteroInicial(), pos),
//                    "Los numeros naturales no pueden tener mas de un punto",
//                    LexicalError.ErrorType.NUMERO_FLOTANTE_INVALIDO
//            ));
            ctx.setTokenActual(new Token(404, linea.substring(ctx.getPunteroInicial(), pos), TiposTokens.INVALIDO));
            ctx.setPunteroFinal(pos);
            return false;
        }

        if (pos < linea.length() && Character.isLetter(linea.charAt(pos))) {
            // Eminente error, inicio con numero pero letra despues? eso no es
            // siquiera intento de identificador
            return false;
        }

        // Token valido
        ctx.setPunteroFinal(pos);
        if (ctx.limitador()) {
            // ctx.agregarToken(new Token(ATRIBUTO, linea.substring(ctx.getPunteroInicial(), ctx.getPunteroFinal()), TiposTokens.NUMERO_FLOTANTE));
            ctx.setTokenActual(new Token(ATRIBUTO, linea.substring(ctx.getPunteroInicial(), ctx.getPunteroFinal()), TiposTokens.NUMERO_FLOTANTE));
            return true;
        }


        ctx.consumirLexema();
        return true;
    }
}
