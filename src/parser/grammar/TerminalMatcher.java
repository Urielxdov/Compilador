package parser.grammar;

import lexer.Token;

public interface TerminalMatcher {
    boolean acepta(Token token);
}
