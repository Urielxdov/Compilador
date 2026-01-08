package data_structures;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Pila
 * ----
 * Implementacion de una pila (Stack) generica basada
 * en una lista enlazada simple
 *
 * Propiedades:
 * - Estructura LIFO (Last-In, First-Out)
 * - Insercion y eliminacion en O(1)
 * - No impone limite de tamaño
 *
 * Caracteristicas:
 * - Implementacion rpopia de nodos enlazados
 * - Iterator desde el tope hasta el fondo
 * - Ecapsulacion total de la estructura interna
 *
 * Uso tipico:
 * - Evaluacion de expresiones
 * - Analisis sintatico
 *
 *
 * @param <T> Tipo de elementos almacenados
 */
public class Pila <T> implements Iterable<T>{
    /**Nodo que representa el tope de la pila*/
    private Nodo<T> tope;
    /**Numero de elementos en la pila*/
    private int size = 0;

    public Pila() {
        tope = null;
    }

    /**
     * Indica si la pila se encuentra vacia
     *
     * @return true si no contiene elementos
     */
    public boolean esVacia() {
        return tope == null;
    }


    /**
     * Inserta un elemento en el tope de la pila
     *
     * @param valor elemento a insertar
     */
    public void push(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);
        nuevo.siguiente = tope;
        size++;
        tope = nuevo;
    }

    /**
     * Elimina y retorna el elemento en el tope de la pila
     *
     * @return elemento removido
     * @throws RuntimeException si la pila esta vacia
     */
    public T pop() {
        if (esVacia()) throw new RuntimeException("La pila se encuentra vacia");
        T valor = tope.valor;
        tope = tope.siguiente;
        size--;
        return valor;
    }

    /**
     * Retorna el elemento en el tope de la pila
     * sin removerlo
     *
     * @return elemento en el tope
     * @throws RuntimeException si la pila esta vacia
     */
    public T peek() {
        if (esVacia()) throw new RuntimeException("La pila se encuentra vacia");
        return tope.valor;
    }

    /**
     * Retorna un iterador que recorre la pila
     * desde el tope hasta el fondo
     */
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

    /**
     * Nodo interno de la pila.
     * Encapsula el valor y la referencia
     * al sigueinte elemento
     */
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
