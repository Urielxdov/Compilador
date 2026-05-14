package semantic.ast;

import lexer.Token;

public class IdentifierNode extends ASTNode {
    private final String name;

    public IdentifierNode(Token token) {
        this.name = token.getLexema();
    }

    public IdentifierNode(String name) {
        this.name = name;
    }

    public String getValue() { return name; }

    @Override
    public String toString() { return "Id(" + name + ")"; }
}
