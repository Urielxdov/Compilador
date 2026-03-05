package semantic.operations;

public class NumberToken implements OperationToken {

    private final String value;

    public NumberToken(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public boolean isOperator() {
        return false;
    }

    @Override
    public String toString() {
        return value;
    }
}
