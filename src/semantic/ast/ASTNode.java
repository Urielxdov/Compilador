package semantic.ast;

import data_structures.Lista;

public abstract class ASTNode {
    protected Lista<ASTNode> children = new Lista<>();
    private int lineNumber;

    public Lista<ASTNode> getChildren() { return children; }
    public void addChild(ASTNode node) { children.agregar(node); }

    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
}
