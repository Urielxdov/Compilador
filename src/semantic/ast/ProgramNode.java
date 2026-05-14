package semantic.ast;

import java.util.ArrayList;
import java.util.List;

public class ProgramNode extends ASTNode {
    private String name;
    private final List<StatementNode> statements = new ArrayList<>();

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
    public void addStatement(StatementNode s) { statements.add(s); }
    public List<StatementNode> getStatements() { return statements; }

    @Override
    public String toString() {
        return "Program(" + name + ", " + statements + ")";
    }
}
