package semantic.operations;

public class OperatorToken implements OperationToken {

    private final String symbol;
    private final int precedence;

    public OperatorToken(String symbol) {
        this.symbol = symbol;
        this.precedence = switch (symbol) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "(", ")" -> 0;
            default -> throw new IllegalArgumentException();
        };
    }

    public int getPrecedence() {
        return precedence;
    }

    public boolean isLeftParenthesis() {
        return symbol.equals("(");
    }

    public boolean isRightParenthesis() {
        return symbol.equals(")");
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isOperator() {
        return true;
    }

    @Override
    public String toString() {
        return symbol;
    }
}