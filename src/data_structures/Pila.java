package data_structures;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Pila <T> implements Iterable<T>{
    private Nodo<T> tope;
    private int size = 0;

    public Pila() {
        tope = null;
    }

    public boolean esVacia() {
        return tope == null;
    }

    public void push(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);
        nuevo.siguiente = tope;
        size++;
        tope = nuevo;
    }

    public T pop() {
        if (esVacia()) throw new RuntimeException("La pila se encuentra vacia");
        T valor = tope.valor;
        tope = tope.siguiente;
        size--;
        return valor;
    }

    public T peek() {
        if (esVacia()) throw new RuntimeException("La pila se encuentra vacia");
        return tope.valor;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int restantes = size;
            private Nodo<T> actual = tope;
            @Override
            public boolean hasNext() {
                return restantes != 0;
            }

            @Override
            public T next() {
                if (restantes <= 0) throw new NoSuchElementException();

                T valor = actual.valor;
                actual = actual.siguiente;
                restantes--;
                return valor;
            }
        };
    }

    private class Nodo<T> {
        Nodo<T> siguiente;
        T valor;

        public Nodo (T valor) {
            this.valor = valor;
            this.siguiente = null;
        }

        @Override
        public String toString() {
            return valor.toString();
        }
    }

    @Override
    public String toString() {
        String cadena = "Pila{" +
                "tope=" + tope +
                ", size=" + size +
                '}' + "\nValores: \n";
        for(T v : this) {
            cadena += v.toString();
        }
        return cadena;
    }
}
