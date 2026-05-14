package semantic;

import semantic.ast.ProgramNode;
import java.util.List;

public class SemanticResult {
    private final boolean success;
    private final List<String> errors;
    private final ProgramNode program;

    public SemanticResult(boolean success, List<String> errors, ProgramNode program) {
        this.success = success;
        this.errors = List.copyOf(errors);
        this.program = program;
    }

    public boolean isSuccess() { return success; }
    public List<String> getErrors() { return errors; }
    public ProgramNode getProgram() { return program; }
}
