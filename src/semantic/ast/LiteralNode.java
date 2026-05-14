package semantic.ast;

import lexer.Token;

public class LiteralNode extends ASTNode {
    private final String value;
    private final boolean floatLiteral;

    public LiteralNode(Token token) {
        this.value = token.getLexema();
        this.floatLiteral = token.getLexema().contains(".");
    }

    public LiteralNode(String value, boolean isFloat) {
        this.value = value;
        this.floatLiteral = isFloat;
    }

    public String getValue() { return value; }
    public boolean isFloat() { return floatLiteral; }

    @Override
    public String toString() { return "Lit(" + value + ")"; }
}
