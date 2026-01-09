package data_structures;

import java.util.Iterator;

/**
 * Implementacion simple de un mapo clave-valor basada en lista enlazada
 *
 * - No utiliza Hashing
 * - Inserciones se realizan al inicio de la lista
 * - La busqueda es lineal
 *
 * Diseñada con fines academicos para evitarrr el uso de java.util.Map
 */

public class Map <K, V> implements Iterable<K>{
    // Cabeza de la lista enlazada
    private Nodo<Par<K, V>> cabeza;

    /**
     * Inserta un par clave-valor en el mapa.
     * Si la clave existe, se actualiza el valor
     */
    public void put (K clave, V valor) {
        Nodo<Par<K, V>> actual = cabeza;

        while (actual != null) {
            if (actual.dato.clave.equals(clave)) {
                actual.dato.valor = valor; // actualiza el valor
                return;
            }
            actual = actual.siguiente;
        }
        // Insertamos al inicio de la lista
        Nodo<Par<K, V>> nodo = new Nodo<>(new Par<>(clave, valor));
        nodo.siguiente = cabeza;
        cabeza = nodo;
    }


    /**
     * Obtiene el valor asociado a una clave
     * RRetorna null si la clave no existe
     */
    public V get (K clave) {
        Nodo <Par<K, V>> actual = cabeza;

        while (actual != null) {
            if (actual.dato.clave.equals(clave)) {
                return actual.dato.valor;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    /**
     * Verifica si una clave existe dentro del mapa
     */
    public boolean existKey (K clave) {
        Nodo <Par<K, V>> actual = cabeza;

        while (actual != null) {
            if (actual.dato.clave.equals(clave)) return true;
            actual = actual.siguiente;
        }

        return false;
    }

    /**
     * Permite iterar sobrer las claves almacenadas en el mapa
     */
    @Override
    public Iterator<K> iterator() {
        return new Iterator<K>() {
            private Nodo<Par<K, V>> actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public K next() {
                K clave = actual.dato.clave;
                actual = actual.siguiente;
                return clave;
            }
        };
    }

    /**
     * Nodo de la lista enlazada
     */
    private class Nodo <T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }

        @Override
        public String toString() {
            return dato.toString();
        }
    }

    /**
     * Estructura para clave-valor
     */
    private class Par <K, V> {
        K clave;
        V valor;

        Par (K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
        }

        @Override
        public String toString() {
            return "Par{" +
                    "clave=" + clave +
                    ", valor=" + valor +
                    '}';
        }
    }

    @Override
    public String toString() {
        String sb = "{\n";
        Nodo<Par<K, V>> actual = cabeza;

        while (actual != null) {
            sb += actual.dato.clave;
            sb += " " + '=';
            sb += actual.dato.valor;

            if (actual.siguiente != null) sb += "\n";

            actual = actual.siguiente;
        }

        sb += "\n}";
        return sb;
    }
}
