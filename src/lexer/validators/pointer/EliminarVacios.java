package lexer.validators.pointer;

import lexer.Context;

/**
 * Ajustador de puntero que avanza el inicio y el fin del lexema
 * ignorando espacios en blanco consecuntivos
 *
 * Se ejecuta antes del reconocimeinto de tokens para garantizar
 * que el lexer comience siempre en un caracter significativo
 */
public class EliminarVacios implements AjustadorPuntero {
    @Override
    public void aplicar(Context ctx) {
        String linea = ctx.getLineaActual();
        int puntero = ctx.getPunteroFinal();
        while(puntero < linea.length() && linea.charAt(puntero) == ' ') {
            puntero++;
        }
        ctx.setPunteroFinal(puntero);
        ctx.setPunteroInicial(puntero);
    }
}
