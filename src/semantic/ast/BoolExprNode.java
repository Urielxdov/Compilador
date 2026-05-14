package semantic.ast;

public class BoolExprNode extends ASTNode {
    private final ExprNode left;
    private final String operator;
    private final ExprNode right;

    public BoolExprNode(ExprNode left, String operator, ExprNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public ExprNode getLeft() { return left; }
    public String getOperator() { return operator; }
    public ExprNode getRight() { return right; }

    @Override
    public String toString() { return "BoolExpr(" + left + " " + operator + " " + right + ")"; }
}
