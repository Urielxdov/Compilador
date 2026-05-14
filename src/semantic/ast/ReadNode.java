package semantic.ast;

import java.util.List;

public class ReadNode extends StatementNode {
    private final List<String> vars;

    public ReadNode(List<String> vars) {
        this.vars = List.copyOf(vars);
    }

    public List<String> getVars() { return vars; }

    @Override
    public String toString() { return "Read(" + vars + ")"; }
}
