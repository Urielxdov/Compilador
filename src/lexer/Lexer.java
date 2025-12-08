package lexer;

import data_structures.Lista;
import lexer.handlers.CaracterSimpleHandler;
import lexer.handlers.NumeroFloatHandler;
import lexer.handlers.NumeroNaturalesHandler;
import lexer.handlers.TokenHandler;
import lexer.validators.boundaries.FinalLexema;
import lexer.validators.boundaries.IdentificadorLimites;
import lexer.validators.pointer.AjustadorPuntero;
import lexer.validators.pointer.EliminarVacios;

public class Lexer {
    private final Lista<TokenHandler> tokenHandlers;
    private final Context ctx;

    private AjustadorPuntero limpiador;

    private IdentificadorLimites limitador;

    public Lexer() {
        ctx = new Context();
        limpiador = new EliminarVacios();
        limitador = new FinalLexema();
        tokenHandlers = new Lista<>();
        tokenHandlers.agregar(new NumeroNaturalesHandler());
        tokenHandlers.agregar(new NumeroFloatHandler());
        tokenHandlers.agregar(new CaracterSimpleHandler());
    }

    public void ejecutar() {
        while (!ctx.finArchivo()) {
            limpiador.aplicar(ctx);

            boolean tokenReconocido = false;

            for (int i = 0; i < tokenHandlers.nodosExistentes(); i++) {
                if(tokenHandlers.obtener(i).proccessChar(ctx)) {
                    tokenReconocido = true;
                    break;
                }
            }

        }

        System.out.println(ctx.getTokens().toString());
        System.out.println(ctx.getErrores().toString());
    }
}
