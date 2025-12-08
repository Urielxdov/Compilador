package lexer.handlers;

import lexer.Context;

public class IdentificadoresHandler implements TokenHandler {
    @Override
    public boolean accept(char c) {
        return (c >= 97 && c <= 122) || (c >= 48 && c <= 57) || (c == 95);
    }

    @Override
    public boolean proccessChar(Context ctx) {

    }
}
