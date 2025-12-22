package parser.grammar;

import data_structures.Lista;

public class Production {
    private final NoTerminal izquierda;
    private final Lista<Symbol> derecha;

    public Production(NoTerminal izquierda, Lista<Symbol> derecha) {
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    public NoTerminal getIzquierda() {
        return izquierda;
    }

    public Lista<Symbol> getDerecha() {
        return derecha;
    }

    @Override
    public String toString() {
        return izquierda.getNombre() + ' ' + derecha.toString();
    }
}
