package parser.grammar;

import data_structures.Lista;

/**
 * Representa una produccion de una gramatica libre de contexto
 * de la forma:
 *
 *      A → α
 *
 * donde {@code A} es un {@link NoTerminal} y α es una
 * secuencia de {@link Symbol}
 *
 * Cada produccion posee un identificador unico que permite
 * distinguirla dentro de la gramatica y facilita su uso en
 * algoritmos de analisis sintactico
 */
public class Production {
    private final int id;                   // Identificiador unico de la produccion
    private final NoTerminal izquierda;     // Lado izquierdo (no terminal)
    private final Lista<Symbol> derecha;    // Lado derecho (secuencia de simbolos)

    public Production(NoTerminal izquierda, Lista<Symbol> derecha, int id) {
        this.izquierda = izquierda;
        this.derecha = derecha;
        this.id = id;
    }

    public NoTerminal getIzquierda() {
        return izquierda;
    }

    public Lista<Symbol> getDerecha() {
        return derecha;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.valueOf(id) + izquierda.getNombre() + ' ' + derecha.toString();
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
