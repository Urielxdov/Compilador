package semantic.operations;

public class IdentifierToken implements OperationToken {
    private final String name;

    public IdentifierToken(String name) { this.name = name; }
    public String getName() { return name; }

    @Override public boolean isNumber() { return false; }
    @Override public boolean isOperator() { return false; }
    @Override public boolean isIdentifier() { return true; }

    @Override public String toString() { return name; }
}
