package lexer;

import data_structures.Lista;
import lexer.constants.TiposTokens;
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
//    public Token getNextToken() {
//        while (!ctx.finArchivo()) {
//            limpiador.aplicar(ctx);
//
//            for (int i = 0; i < tokenHandlers.nodosExistentes(); i++) {
//                if(tokenHandlers.obtener(i).proccessChar(ctx)) break;
//            }
//            ctx.setPunteroFinal(ctx.getPunteroFinal()-1);
//            if (ctx.limitador()) {
//                ctx.setPunteroFinal(ctx.getPunteroFinal()+1);
//            } else {
//                ctx.setPunteroFinal(ctx.getPunteroFinal()+1);
//                ctx.consumirLexema();
//            }
//            return ctx.getTokenActual();
//        }
//        return null;
//    }

    public Token next() {
        if (ctx.finArchivo()) {
            return new Token(-1, "$", TiposTokens.IDENTIFICADOR);
        }
        limpiador.aplicar(ctx);

        for (TokenHandler handler : tokenHandlers) {
            if(handler.proccessChar(ctx)) break;
        }

        ctx.setPunteroFinal(ctx.getPunteroFinal() - 1);
        if (ctx.limitador()) {
            ctx.setPunteroFinal(ctx.getPunteroFinal() + 1);
        } else {
            ctx.setPunteroFinal(ctx.getPunteroFinal() + 1);
            ctx.consumirLexema();
        }
        return  ctx.getTokenActual();
    }
}
