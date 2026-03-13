package semantic.operations;

public class OperatorToken implements OperationToken {

    private final String symbol;
    private final int precedence;

    public OperatorToken(String symbol) {
        this.symbol = symbol;

        switch (symbol) {
            case "+":
            case "-":
                this.precedence = 1;
                break;

            case "*":
            case "/":
                this.precedence = 2;
                break;

            case "(":
            case ")":
                this.precedence = 0;
                break;

            default:
                throw new IllegalArgumentException();
        }
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