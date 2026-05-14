package semantic.ast;

public class AssignmentNode extends StatementNode {
    private final String target;
    private final ExprNode expression;

    public AssignmentNode(String target, ExprNode expression) {
        this.target = target;
        this.expression = expression;
    }

    public String getTarget() { return target; }
    public ExprNode getExpression() { return expression; }

    @Override
    public String toString() { return "Assign(" + target + " = " + expression + ")"; }
}
