package lexer.handlers;

import lexer.Context;
import lexer.Token;
import lexer.handlers.errors.LexicalError;

public class NumeroNaturalesHandler implements  TokenHandler{
    private final int ATRIBUTO = 400;
    
    @Override
    public boolean accept(char c) {
        return (c >= 48) && (c <= 57);
    }

    @Override
    public boolean proccessChar(Context ctx) {

        String linea = ctx.getLineaActual();
        int pos = ctx.getPunteroFinal();
        
        if (ctx.getPunteroFinal() >= linea.length()) return false; // Posiblemente error
        
        char c = linea.charAt(pos);

        if (!accept(c)) return false;

        boolean empiezaCero = c == 48;
        pos++;

        while (pos < linea.length() && accept(linea.charAt(pos))) pos++;

        if (empiezaCero && (pos >= linea.length() || linea.charAt(pos) != '.')) {
            // Empezo con 0 pero no es un flotante
            // 0123 -> error
            ctx.agregarError(new LexicalError(ctx.getNumeroLinea(), pos, linea.substring(ctx.getPunteroInicial(), pos), "Los numeros naturales no pueden comenzar por 0", LexicalError.ErrorType.NUMERO_NATURAL_INVALIDO));
            ctx.setPunteroFinal(pos);
            return false;
        }

        // Aqui no es el handler correcto
        // Es un flotante posiblemente
        if (pos < linea.length() && linea.charAt(pos) == '.') return false;

        if (empiezaCero) {
            // error lexico dado que puede ser algo como
            // 0avs_12 -> los identificadores empiezan con letras
            String lexema = ctx.consumirLexema();
            return false;
        }

        // Posible identificador o palabra reservada
        if (pos < linea.length() && Character.isLetter(linea.charAt(pos))) {
            ctx.setPunteroFinal(pos);
            return false;
        }

        // Token valido
        ctx.setPunteroFinal(pos);
        if (ctx.limitador()) {
            ctx.agregarToken(new Token(ATRIBUTO, linea.substring(ctx.getPunteroInicial(), ctx.getPunteroFinal())));
            return true;
        }

        // Vemos como validar si no es un caracter especial
        ctx.consumirLexema();
        return true;
    }
}
