package lexer.handlers;

import lexer.Context;

public interface TokenHandler {
    boolean accept(char c);
    boolean proccessChar(Context ctx);
}
