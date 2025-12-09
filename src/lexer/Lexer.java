package lexer;

import data_structures.Lista;
import lexer.handlers.*;
import lexer.validators.pointer.AjustadorPuntero;
import lexer.validators.pointer.EliminarVacios;

public class Lexer {
    private final Lista<TokenHandler> tokenHandlers;
    private final Context ctx;

    private AjustadorPuntero limpiador;

    public Lexer() {
        ctx = new Context();
        limpiador = new EliminarVacios();
        tokenHandlers = new Lista<>();
        tokenHandlers.agregar(new NumeroNaturalesHandler());
        tokenHandlers.agregar(new NumeroFloatHandler());
        tokenHandlers.agregar(new IdentificadoresHandler());
        tokenHandlers.agregar(new CaracterSimpleHandler());
    }
    public void ejecutar() {
        while (!ctx.finArchivo()) {
            limpiador.aplicar(ctx);

            for (int i = 0; i < tokenHandlers.nodosExistentes(); i++) {
                if(tokenHandlers.obtener(i).proccessChar(ctx)) break;
            }

            ctx.consumirLexema();
        }
        System.out.println(ctx.getTokens().toString());
        System.out.println(ctx.getErrores().toString());
    }
}
