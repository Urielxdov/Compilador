package semantic.ast;

import java.util.List;

public class WriteNode extends StatementNode {
    private final List<ExprNode> expressions;

    public WriteNode(List<ExprNode> expressions) {
        this.expressions = List.copyOf(expressions);
    }

    public List<ExprNode> getExpressions() { return expressions; }

    @Override
    public String toString() { return "Write(" + expressions + ")"; }
}
