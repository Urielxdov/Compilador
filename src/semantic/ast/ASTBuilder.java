package semantic.ast;

import data_structures.Pila;
import lexer.Token;
import parser.grammar.Production;

import java.util.ArrayList;
import java.util.List;

public class ASTBuilder {
    private Pila<ASTNode> pilaNodos = new Pila<>();

    public void onMatchTerminal(Token token) {

    }

    public void onReduce(Production p) {
        ASTNode nodo; // crea el nodo

        int hijos = p.getDerecha().nodosExistentes();

        List<ASTNode> temp = new ArrayList<>();

        for (int i = 0; i < hijos; i++) {
            temp.add(0, pilaNodos.pop());
        }
    }
}
