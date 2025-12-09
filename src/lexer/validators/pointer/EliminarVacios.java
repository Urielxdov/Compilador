package lexer.validators.pointer;

import lexer.Context;

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
