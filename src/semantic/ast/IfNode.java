package semantic.ast;

public class IfNode extends StatementNode {
    private final BoolExprNode condition;
    private final StatementNode thenBranch;
    private final StatementNode elseBranch;

    public IfNode(BoolExprNode condition, StatementNode thenBranch, StatementNode elseBranch) {
        if (condition == null || thenBranch == null || elseBranch == null)
            throw new IllegalArgumentException("IfNode: condition, thenBranch, and elseBranch must not be null");
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public BoolExprNode getCondition() { return condition; }
    public StatementNode getThenBranch() { return thenBranch; }
    public StatementNode getElseBranch() { return elseBranch; }

    @Override
    public String toString() { return "If(" + condition + " ? " + thenBranch + " : " + elseBranch + ")"; }
}
