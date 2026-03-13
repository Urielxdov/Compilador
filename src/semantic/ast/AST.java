package semantic.ast;

public class AST {
    private ASTNode raiz;

    public AST(ASTNode raiz) {
        this.raiz = raiz;
    }

    public ASTNode getRaiz() {
        return raiz;
    }
}
