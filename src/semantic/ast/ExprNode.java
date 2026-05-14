package semantic.ast;

import semantic.operations.IdentifierToken;
import semantic.operations.OperationToken;
import java.util.ArrayList;
import java.util.List;

public class ExprNode extends ASTNode {
    private final List<OperationToken> tokens = new ArrayList<>();

    public void addToken(OperationToken t) { tokens.add(t); }
    public List<OperationToken> getTokens() { return tokens; }

    public boolean isSingleIdentifier() {
        return tokens.size() == 1 && tokens.get(0).isIdentifier();
    }

    public String getSingleIdentifier() {
        if (!isSingleIdentifier()) {
            throw new IllegalStateException("ExprNode is not a single identifier: " + tokens);
        }
        return ((IdentifierToken) tokens.get(0)).getName();
    }

    @Override
    public String toString() { return "Expr" + tokens; }
}
