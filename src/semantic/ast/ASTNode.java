package semantic.ast;

import lexer.Token;

public class ASTNode {
    private Token token;
    private ASTNode izquierdo;
    private ASTNode derecho;

    public ASTNode(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public ASTNode getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(ASTNode izquierdo) {
        this.izquierdo = izquierdo;
    }

    public ASTNode getDerecho() {
        return derecho;
    }

    public void setDerecho(ASTNode derecho) {
        this.derecho = derecho;
    }
}
