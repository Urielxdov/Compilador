package data_structures;

import java.util.Iterator;

public class Lista<T> implements Iterable<T> {
    private Nodo<T> inicio;
    private Nodo<T> fin;
    private int numeroNodos = 0;

    public Lista() {}

    public void agregar(T valor) {
        agregar(valor, true);
    }

    private Nodo<T> agregar(T valor, boolean diferenciador) {
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


    public int nodosExistentes(){
        return  this.numeroNodos;
    }

    public T eliminar() {
        Nodo<T> eliminado = inicio;
        inicio = inicio.siguiente;
        eliminado.siguiente = null;
        this.numeroNodos--;
        return eliminado.valor;
    }

    public boolean existe(T valor) {
        Nodo<T> nodoActual = this.inicio;
        for (int i = 0; i < this.numeroNodos; i++) {
            if (valor.equals(nodoActual.valor)) return true;
            nodoActual = nodoActual.siguiente;
        }
        return false;
    }

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