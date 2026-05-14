package semantic.ast;

import assembler.DataType;
import java.util.List;

public class DeclarationNode extends StatementNode {
    private final DataType type;
    private final List<String> ids;

    public DeclarationNode(DataType type, List<String> ids) {
        this.type = type;
        this.ids = List.copyOf(ids);
    }

    public DataType getType() { return type; }
    public List<String> getIds() { return ids; }

    @Override
    public String toString() { return "Decl(" + type + " " + ids + ")"; }
}
