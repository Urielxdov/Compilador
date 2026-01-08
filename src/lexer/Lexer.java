package lexer;

import data_structures.Lista;
import lexer.constants.TiposTokens;
import lexer.handlers.*;
import lexer.validators.pointer.AjustadorPuntero;
import lexer.validators.pointer.EliminarVacios;

/**
 * Lexer (Analizador Lexico)
 *
 * Responsable de convertir una secuencia de caracteres en una
 * secuencia de tokens utilizando un enfoque basado en handlers (Cadena de responsabilidad)
 *
 * Funcionamiento general:
 * 1. Mantiene un contexto de lectura (Context) que controla punteros y lexema
 * 2. Aplica validadores de puntero antes de procesar cada token (ej. elminar espacios que limpia caracteres inservibles)
 * 3. Itera sobre una lista de TokenHanlder hasta que uno reconoce el token
 * 4. Ajusta punteros y consume el lexema correspondiente
 *
 * El lexer no reconoce reglas especificas de tpkens, dichas reglas
 * estan encapsuladas en los TokenHandler
 *
 * Extensibilidad
 * - Para agregar un nuevo tipo de token, basta con implmentar TokenHanlder
 * y registrarlo en el contructor
 *
 * Patron de diseño:
 * - Chain of Responsibility
 */
public class Lexer {
    /**
     * Lista ordenada de hanlders responsables de reconocer tokens.
     * El orden es importante: el primer handler que reconoce el token
     * detiene la cadea
     */
    private final Lista<TokenHandler> tokenHandlers;
    /**
     * Contexto com´partido del analisis lexico.
     * Mantiene punteros, lexema actual y token reconocido.
     */
    private final Context ctx;

    /**
     * Validador/Ajustador del puntero antes de procesar un token
     * Elimina espacios, saltos de linea u otros caracteres ignorables
     */
    private AjustadorPuntero limpiador;

    /**
     * Inicializa el lexer con su contexto y registra los handlers
     * basicos soportados por el lenguaje
     *
     * El orden de registro define la prioridad de reconocimiento, siendo
     * los numeros naturales y flotantes los de mas alta prioridad
     */
    public Lexer() {
        ctx = new Context();
        limpiador = new EliminarVacios();
        tokenHandlers = new Lista<>();
        // Primero se validan posibilidades de numeros
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


    /**
     * Obtiene el siguiente token valido del flujo de entrada
     *
     * Flujo interno:
     * 1. Verifica fin de archivo
     * 2. Aplica validadores de puntero (ej. eliminacion de espacios)
     * 3. Delega el reconocimiento del token a los TokenHandlers
     * 4. Ajusta punteros finales segun si se presentan caracteres delimitadores
     * 5. Consume el lexema y devuelve el token actual
     *
     * @return - El siguiente Token reconocido o un token especial de fin de archivo
     */
    public Token next() {
        if (ctx.finArchivo()) {
            return new Token(-1, "$", TiposTokens.IDENTIFICADOR);
        }
        limpiador.aplicar(ctx);

        for (TokenHandler handler : tokenHandlers) {
            if(handler.proccessChar(ctx)) break;
        }

        // Retrocede el puntero final por que el handler avanza una posicion extra
        // y causaba errores dado que si se encontraban lexemas validos juntos, estos se perdian
        ctx.setPunteroFinal(ctx.getPunteroFinal() - 1);
        // Si el token es delimitador, no se consume el lexema (ej. Caracter simple)
        if (ctx.limitador()) {
            ctx.setPunteroFinal(ctx.getPunteroFinal() + 1);
        } else {
            // Tokens normales consumen lexema
            ctx.setPunteroFinal(ctx.getPunteroFinal() + 1);
            ctx.consumirLexema();
        }
        return  ctx.getTokenActual();
    }
}
