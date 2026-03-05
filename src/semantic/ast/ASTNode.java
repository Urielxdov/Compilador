package semantic.ast;

import data_structures.Lista;

public abstract class ASTNode {
    protected Lista<ASTNode> children = new Lista<>();

    public Lista<ASTNode> getChildren() {
        return children;
    }

    public void addChild(ASTNode node) {
        children.agregar(node);
    }
}
