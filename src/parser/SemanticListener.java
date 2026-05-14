// src/parser/SemanticListener.java
package parser;

import lexer.Token;
import parser.grammar.Production;

public interface SemanticListener {
    void onProductionApplied(Production p, Token lookahead);
    void onTerminalMatched(Token token);
    void onParseComplete(boolean success);
}
