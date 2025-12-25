package grammar.utils;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Production other = (Production) o;
        return izquierda.equals(other.izquierda) && derecha.equals(other.derecha);
    }

    @Override
    public int hashCode() {
        int resultado = izquierda.hashCode();
        resultado = 31 * resultado + derecha.hashCode(); // El 31 es un numero primo pequeño, reduce coalisiones
        return resultado;
    }
}
