package semantic.ast;

import lexer.Token;
import java.util.ArrayList;
import java.util.List;

public class ASTNode {
    private Token token;
    private ASTNode izquierdo;
    private ASTNode derecho;
    private NodeKind kind;
    private List<ASTNode> hijos;

    public ASTNode(Token token) {
        this.token = token;
        this.hijos = new ArrayList<>();
    }

    public ASTNode(NodeKind kind) {
        this.kind = kind;
        this.hijos = new ArrayList<>();
    }

    public ASTNode(NodeKind kind, Token token) {
        this.kind = kind;
        this.token = token;
        this.hijos = new ArrayList<>();
    }

    public void agregarHijo(ASTNode hijo) {
        hijos.add(hijo);
    }

    public Token getToken() { return token; }
    public ASTNode getIzquierdo() { return izquierdo; }
    public void setIzquierdo(ASTNode izquierdo) { this.izquierdo = izquierdo; }
    public ASTNode getDerecho() { return derecho; }
    public void setDerecho(ASTNode derecho) { this.derecho = derecho; }
    public NodeKind getKind() { return kind; }
    public List<ASTNode> getHijos() { return hijos; }
}
