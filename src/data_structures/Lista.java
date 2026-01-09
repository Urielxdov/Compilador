package data_structures;

import java.util.Iterator;

/**
 * Implementacion simple de una lista simple enlazada
 *
 * - Insersiciones que se realizan de manera consecutiva
 * - Busqueda lineal
 *
 * Diseñada con fines academicos para evitar el uso de java.util.List
 */
public class Lista<T> implements Iterable<T> {
    // Inicio de la lista
    private Nodo<T> inicio;
    // Final de la lista
    private Nodo<T> fin;
    // Cantidad de elementos registrados
    private int numeroNodos = 0;

    public Lista() {}

    /**
     * Inserta un nuevo elemento en la lista
     * Si la lista esta vacia se asigna al inicio y fin el nuevo elemento
     */
    public Nodo<T> agregar(T valor) {
        this.numeroNodos++;
        if (this.inicio == null) {
            this.inicio = new Nodo<>(valor);
            this.fin = this.inicio;
            return this.inicio;
        }
        Nodo<T> nuevoNodo = new Nodo<>(valor);
        this.fin.siguiente = nuevoNodo;
        this.fin = nuevoNodo;
        return this.fin;
    }

    /**
     * @return - Numero de elementos registrados
     */
    public int nodosExistentes(){
        return  this.numeroNodos;
    }

    /**
     * Itera sobre la lista para encontrar un elemento que concida con el parametro
     * Busqueda lineal
     * @return - (boolean) Indica la presencia o ausencia del elemento buscado
     */
    public boolean existe(T valor) {
        Nodo<T> nodoActual = this.inicio;
        for (int i = 0; i < this.numeroNodos; i++) {
            if (valor.equals(nodoActual.valor)) return true;
            nodoActual = nodoActual.siguiente;
        }
        return false;
    }

    /**
     * Busqueda lineal por indice de elemento
     * @return - Elemento de la posicion 'i'
     */
    public T obtener(int i) {
        if (i < 0 || i >= nodosExistentes()) return null; // Validación de índice

        Nodo<T> actual = inicio;
        for(int j = 0; j < i; j++) { // Cambié la condición del bucle
            if (actual == null) return null; // Protección adicional
            actual = actual.siguiente; // Cambié inicio.siguiente por actual.siguiente
        }
        return actual != null ? actual.valor : null;
    }


    @Override
    public String toString() {
        String cadena = "";
        for(T v : this) {
            cadena += v.toString() + '\n';
        }
        return cadena;
    }

    /**
     * Permite iterarr sobre los elementos de la lista
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int restantes = numeroNodos;
            private Nodo<T> nodoActual = inicio;
            @Override
            public boolean hasNext() {
                return restantes > 0;
            }

            @Override
            public T next() {
                Nodo<T> valor = nodoActual;
                nodoActual = nodoActual.siguiente;

                restantes--;
                return valor.valor;
            }
        };
    }

    /**
     * Nodo de la lista simplemente enlazada
     */
    private static class Nodo<E> {
        E valor;
        Nodo<E> siguiente;

        public Nodo(E valor) {
            this.valor = valor;
        }

        @Override
        public String toString() {
            return "Nodo{" +
                    "valor=" + valor +
                    ", siguiente=" + siguiente +
                    '}';
        }
    }
}