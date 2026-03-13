package semantic.ast;

import data_structures.Lista;
import lexer.Token;

public class ASTBuilder {

    public AST construirAST(Lista<Token> tokens) {
        if (tokens.nodosExistentes() != 3){
            throw new RuntimeException("Expresion no soportada todavia");
        }

        Token izquierdo = tokens.obtener(0);
        Token operador = tokens.obtener(1);
        Token derecho = tokens.obtener(2);

        ASTNode nodoIzq = new ASTNode(izquierdo);
        ASTNode nodoDer = new ASTNode(derecho);
        ASTNode raiz = new ASTNode(operador);

        raiz.setIzquierdo(nodoIzq);
        raiz.setDerecho(nodoDer);

        return new AST(raiz);

    }
}
